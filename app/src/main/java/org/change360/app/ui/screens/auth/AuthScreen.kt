package org.change360.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.change360.app.Change360Application
import org.change360.app.di.AppContainer
import org.change360.app.di.Change360ViewModelFactory
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.components.GlassmorphicCard
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Stateful entry composable for the CHANGE360 Authentication and Onboarding screen.
 *
 * Connects to [AuthViewModel] via [Change360ViewModelFactory], collects [AuthUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and delegates rendering
 * to the stateless [AuthContent] composable.
 *
 * @param onAuthSuccess Callback invoked when the user successfully authenticates.
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing authentication credentials, pledges, and submission.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: AuthViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthSuccess()
            viewModel.resetSuccess()
        }
    }

    AuthContent(
        uiState = uiState,
        onModeChanged = viewModel::onModeChanged,
        onDisplayNameChanged = viewModel::onDisplayNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPledgeSelected = viewModel::onPledgeSelected,
        onCustomPledgeChanged = viewModel::onCustomPledgeChanged,
        onSubmit = viewModel::submit,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for practitioner onboarding and sign-in.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthContent(
    uiState: AuthUiState,
    onModeChanged: (AuthMode) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPledgeSelected: (String) -> Unit,
    onCustomPledgeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Change360TopBar(
            title = "CHANGE360",
            subtitle = "Planetary Stewardship Onboarding"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selector Tabs (Sign Up / Sign In / Quick Pledge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AuthMode.entries.forEach { authMode ->
                    val isSelected = authMode == uiState.mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) BioLuminescentEmerald else Color.Transparent)
                            .clickable {
                                onModeChanged(authMode)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = authMode.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) DeepForestObsidian else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Descriptive Banner Card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BioLuminescentEmerald.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (uiState.mode) {
                                    AuthMode.SIGN_UP -> Icons.Filled.Public
                                    AuthMode.SIGN_IN -> Icons.Filled.LockOpen
                                    AuthMode.GUEST -> Icons.Filled.EnergySavingsLeaf
                                },
                                contentDescription = null,
                                tint = BioLuminescentEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (uiState.mode) {
                                AuthMode.SIGN_UP -> "Create Your Practitioner Profile"
                                AuthMode.SIGN_IN -> "Welcome Back, Earth Guardian"
                                AuthMode.GUEST -> "Fast-Track Guest Onboarding"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (uiState.mode) {
                                AuthMode.SIGN_UP -> "Track carbon reduction, earn aura XP, and claim quest rewards."
                                AuthMode.SIGN_IN -> "Sync your verified planetary logs and continue your streak."
                                AuthMode.GUEST -> "Begin logging eco-actions immediately with zero password friction."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input Fields
            if (uiState.mode == AuthMode.SIGN_UP || uiState.mode == AuthMode.GUEST) {
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = onDisplayNameChanged,
                    label = { Text("Display Name / Call Sign") },
                    placeholder = { Text("e.g. Eco Warrior Alex") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioLuminescentEmerald,
                        cursorColor = BioLuminescentEmerald
                    )
                )
            }

            if (uiState.mode == AuthMode.SIGN_UP || uiState.mode == AuthMode.SIGN_IN) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChanged,
                    label = { Text("Email Address") },
                    placeholder = { Text("practitioner@change360.org") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioLuminescentEmerald,
                        cursorColor = BioLuminescentEmerald
                    )
                )
            }

            // Personal Eco-Pledge Selector Chips
            if (uiState.mode == AuthMode.SIGN_UP || uiState.mode == AuthMode.GUEST) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select Your Foundational Eco-Pledge",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.pledgeOptions.forEach { pledge ->
                            val isSelected = pledge == uiState.selectedPledge && uiState.customPledge.isBlank()
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPledgeSelected(pledge) },
                                label = {
                                    Text(
                                        text = pledge,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BioLuminescentEmerald.copy(alpha = 0.25f),
                                    selectedLabelColor = BioLuminescentEmerald
                                )
                            )
                        }
                    }

                    // Optional Custom Pledge
                    OutlinedTextField(
                        value = uiState.customPledge,
                        onValueChange = onCustomPledgeChanged,
                        label = { Text("Or Author Custom Pledge (Optional)") },
                        placeholder = { Text("I commit to planting 5 native saplings monthly...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SolarCyan,
                            cursorColor = SolarCyan
                        )
                    )
                }
            }

            // Error Message Banner
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CoralAlert.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralAlert.copy(alpha = 0.40f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoralAlert,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss error",
                                tint = CoralAlert,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Submission Button
            Button(
                onClick = onSubmit,
                enabled = uiState.isInputValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BioLuminescentEmerald,
                    disabledContainerColor = BioLuminescentEmerald.copy(alpha = 0.25f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DeepForestObsidian,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = when (uiState.mode) {
                            AuthMode.SIGN_UP -> "Join Movement & Begin Quests"
                            AuthMode.SIGN_IN -> "Sign In to CHANGE360"
                            AuthMode.GUEST -> "Enter as Earth Guardian"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = DeepForestObsidian
                    )
                }
            }

            // Alternative Navigation Helper
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        val nextMode = when (uiState.mode) {
                            AuthMode.SIGN_UP -> AuthMode.SIGN_IN
                            AuthMode.SIGN_IN -> AuthMode.GUEST
                            AuthMode.GUEST -> AuthMode.SIGN_UP
                        }
                        onModeChanged(nextMode)
                    }
                ) {
                    Text(
                        text = when (uiState.mode) {
                            AuthMode.SIGN_UP -> "Already registered? Sign In"
                            AuthMode.SIGN_IN -> "First time here? Try Quick Guest Pledge"
                            AuthMode.GUEST -> "Prefer a permanent profile? Join Movement"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = SolarCyan,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun AuthScreenPreview() {
    Change360Theme(darkTheme = true) {
        AuthContent(
            uiState = AuthUiState(),
            onModeChanged = {},
            onDisplayNameChanged = {},
            onEmailChanged = {},
            onPledgeSelected = {},
            onCustomPledgeChanged = {},
            onSubmit = {},
            onClearError = {}
        )
    }
}

