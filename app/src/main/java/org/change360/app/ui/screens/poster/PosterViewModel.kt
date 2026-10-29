package org.change360.app.ui.screens.poster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.repository.AuthRepository
import org.change360.app.domain.repository.ImpactRepository
import org.change360.app.domain.usecase.SaveGeneratedPosterUseCase
import org.change360.app.domain.usecase.SavePosterRequest

/**
 * Typealias aligning with PLAN.md naming specifications.
 */
typealias PosterGeneratorViewModel = PosterViewModel

/**
 * ViewModel managing advocacy poster customization, live preview styling, verified badge injection,
 * and persistence/export pipelines.
 *
 * Responsibilities:
 * - Manages editable poster headline, personal eco-pledge quote, and aura palette selections.
 * - Controls verified practitioner badge overlay reflecting live user metrics.
 * - Saves poster configurations and links exported bitmap URIs via [SaveGeneratedPosterUseCase].
 *
 * Compatible with [org.change360.app.di.ViewModelFactory] reflection-based constructor resolution.
 *
 * @param saveGeneratedPosterUseCase Core use case persisting poster configurations and constructing badges.
 * @param authRepository Optional repository providing reactive active user profile metrics.
 * @param impactRepository Optional repository providing verified cumulative CO2 mitigation metrics.
 */
class PosterViewModel @JvmOverloads constructor(
    private val saveGeneratedPosterUseCase: SaveGeneratedPosterUseCase,
    private val authRepository: AuthRepository? = null,
    private val impactRepository: ImpactRepository? = null
) : ViewModel() {

    private val _selectedStyle = MutableStateFlow(PosterAuraStyle.BIO_EMERALD)
    private val _headline = MutableStateFlow(PosterUiState.DEFAULT_HEADLINE)
    private val _pledgeQuote = MutableStateFlow(PosterUiState.DEFAULT_QUOTE)
    private val _accentColorHex = MutableStateFlow(PosterAuraStyle.BIO_EMERALD.primaryColorHex)
    private val _includeStatsBadge = MutableStateFlow(true)
    private val _isSaving = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _lastExportedUri = MutableStateFlow<String?>(null)
    private val _lastSavedPosterId = MutableStateFlow<Long?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val activeUserFlow = authRepository?.observeActiveUser() ?: flowOf(null)
    private val totalCo2Flow = impactRepository?.observeTotalCo2Saved() ?: flowOf(0f)

    private val _uiState = MutableStateFlow(
        PosterUiState(templates = saveGeneratedPosterUseCase.getCuratedTemplates())
    )

    /**
     * Primary UI State stream governing live poster rendering and studio controls.
     */
    val uiState: StateFlow<PosterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                saveGeneratedPosterUseCase.observeSavedPosters(),
                activeUserFlow,
                totalCo2Flow
            ) { saved, user, co2 ->
                _uiState.update { current ->
                    current.copy(
                        savedPosters = saved,
                        verifiedBadge = saveGeneratedPosterUseCase.buildVerifiedBadge(user, co2)
                    )
                }
            }.collect()
        }
    }

    /**
     * Updates the poster's visual aura gradient theme and synchronizes the primary accent color.
     */
    fun onStyleSelected(style: PosterAuraStyle) {
        _selectedStyle.value = style
        _accentColorHex.value = style.primaryColorHex
        _errorMessage.value = null
        _uiState.update {
            it.copy(
                selectedStyle = style,
                accentColorHex = style.primaryColorHex,
                errorMessage = null
            )
        }
    }

    /**
     * Updates the main advocacy title/headline text.
     */
    fun onHeadlineChanged(headline: String) {
        _headline.value = headline
        _errorMessage.value = null
        _uiState.update {
            it.copy(
                headline = headline,
                errorMessage = null
            )
        }
    }

    /**
     * Updates the supporting eco-pledge quotation text.
     */
    fun onQuoteChanged(quote: String) {
        _pledgeQuote.value = quote
        _errorMessage.value = null
        _uiState.update {
            it.copy(
                pledgeQuote = quote,
                errorMessage = null
            )
        }
    }

    /**
     * Toggles whether the verified live practitioner impact badge appears on the poster.
     */
    fun onIncludeStatsBadgeToggled(include: Boolean) {
        _includeStatsBadge.value = include
        _uiState.update {
            it.copy(includeStatsBadge = include)
        }
    }

    /**
     * Updates the custom hex color stop used for badge accents and borders.
     */
    fun onAccentColorChanged(colorHex: String) {
        _accentColorHex.value = colorHex
        _uiState.update {
            it.copy(accentColorHex = colorHex)
        }
    }

    /**
     * Populates headlines, quotes, and visual palette from a curated starter template.
     */
    fun applyTemplate(template: PosterTemplate) {
        _headline.value = template.title
        _pledgeQuote.value = template.defaultQuote
        _selectedStyle.value = template.auraStyle
        _accentColorHex.value = template.accentColorHex
        _errorMessage.value = null
        _uiState.update {
            it.copy(
                headline = template.title,
                pledgeQuote = template.defaultQuote,
                selectedStyle = template.auraStyle,
                accentColorHex = template.accentColorHex,
                errorMessage = null
            )
        }
    }

    /**
     * Persists the current poster design configuration into local storage.
     *
     * @param exportedUri Optional bitmap file URI if rendering has already concluded.
     */
    fun savePoster(exportedUri: String? = null) {
        val titleText = _uiState.value.headline.trim()
        val quoteText = _uiState.value.pledgeQuote.trim()

        if (titleText.isBlank() || quoteText.isBlank()) {
            val err = "Poster headline and pledge quote cannot be empty."
            _errorMessage.value = err
            _uiState.update { it.copy(errorMessage = err) }
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val request = SavePosterRequest(
                title = titleText,
                pledgeQuote = quoteText,
                templateStyle = _uiState.value.selectedStyle,
                accentColorHex = _uiState.value.accentColorHex,
                exportedUriString = exportedUri ?: _uiState.value.lastExportedUri
            )

            val result = saveGeneratedPosterUseCase(request)

            result.onSuccess { posterId ->
                _lastSavedPosterId.value = posterId
                _uiState.update { it.copy(lastSavedPosterId = posterId) }
            }.onFailure { error ->
                val err = error.message ?: "Failed to save advocacy poster."
                _errorMessage.value = err
                _uiState.update { it.copy(errorMessage = err) }
            }

            _isSaving.value = false
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    /**
     * Updates the stored image file URI associated with an existing poster record.
     */
    fun updateExportedUri(posterId: Long, exportedUri: String) {
        val cleanUri = exportedUri.trim()
        if (cleanUri.isBlank()) return

        viewModelScope.launch {
            val result = saveGeneratedPosterUseCase.updateExportedUri(posterId, cleanUri)
            result.onSuccess {
                _lastExportedUri.value = cleanUri
                _uiState.update { it.copy(lastExportedUri = cleanUri) }
            }.onFailure { error ->
                val err = error.message ?: "Failed to update exported URI."
                _errorMessage.value = err
                _uiState.update { it.copy(errorMessage = err) }
            }
        }
    }

    /**
     * Signals that off-screen bitmap rendering has begun.
     */
    fun startExport() {
        _isExporting.value = true
        _errorMessage.value = null
        _uiState.update { it.copy(isExporting = true, errorMessage = null) }
    }

    /**
     * Signals that off-screen bitmap capture and FileProvider URI generation has completed.
     *
     * @param exportedUri File URI to be shared via Android Intent.
     */
    fun finishExport(exportedUri: String) {
        _isExporting.value = false
        _lastExportedUri.value = exportedUri
        _uiState.update { it.copy(isExporting = false, lastExportedUri = exportedUri) }

        // If a saved poster ID is active, link the URI atomically
        val currentPosterId = _uiState.value.lastSavedPosterId
        if (currentPosterId != null) {
            updateExportedUri(currentPosterId, exportedUri)
        }
    }

    /**
     * Deletes a saved poster from local history.
     */
    fun deleteSavedPoster(posterId: Long) {
        viewModelScope.launch {
            val result = saveGeneratedPosterUseCase.deletePoster(posterId)
            result.onFailure { error ->
                val err = error.message ?: "Failed to delete saved poster."
                _errorMessage.value = err
                _uiState.update { it.copy(errorMessage = err) }
            }
        }
    }

    /**
     * Clears all saved user posters from history.
     */
    fun clearAllSavedPosters() {
        viewModelScope.launch {
            val result = saveGeneratedPosterUseCase.clearAllPosters()
            result.onFailure { error ->
                val err = error.message ?: "Failed to clear saved posters."
                _errorMessage.value = err
                _uiState.update { it.copy(errorMessage = err) }
            }
        }
    }

    /**
     * Dismisses any active error notification banner.
     */
    fun clearError() {
        _errorMessage.value = null
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Resets the export URI state following share sheet launch.
     */
    fun clearExportedUri() {
        _lastExportedUri.value = null
        _uiState.update { it.copy(lastExportedUri = null) }
    }
}
