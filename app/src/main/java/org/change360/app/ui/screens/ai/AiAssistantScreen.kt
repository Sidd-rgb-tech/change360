package org.change360.app.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.model.MessageSender
import org.change360.app.ui.components.Change360TopBar
import org.change360.app.ui.theme.BioLuminescentEmerald
import org.change360.app.ui.theme.Change360Theme
import org.change360.app.ui.theme.CoralAlert
import org.change360.app.ui.theme.DeepForestObsidian
import org.change360.app.ui.theme.SolarCyan

/**
 * Stateful entry composable for the Eco-AI Coach chat interface.
 *
 * Connects to [AiAssistantViewModel] via [Change360ViewModelFactory], collects [AiAssistantUiState]
 * with lifecycle awareness ([collectAsStateWithLifecycle]), and delegates to [AiAssistantContent].
 *
 * @param modifier Layout modifier applied to the root container.
 * @param appContainer Dependency injection container providing application singletons.
 * @param viewModel ViewModel governing conversational memory, message dispatch, and quick chips.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    modifier: Modifier = Modifier,
    appContainer: AppContainer = Change360Application.appContainer,
    viewModel: AiAssistantViewModel = viewModel(factory = Change360ViewModelFactory(appContainer))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AiAssistantContent(
        uiState = uiState,
        onInputPromptChanged = viewModel::onInputPromptChanged,
        onSendMessage = viewModel::sendMessage,
        onQuickPromptSelected = { chip -> viewModel.onQuickPromptSelected(chip, autoSend = true) },
        onClearHistory = viewModel::clearHistory,
        onDeleteMessage = viewModel::deleteMessage,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for the conversational AI Assistant screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantContent(
    uiState: AiAssistantUiState,
    onInputPromptChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onQuickPromptSelected: (String) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteMessage: (Long) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        val totalCount = uiState.messages.size + if (uiState.isTyping) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Change360TopBar(
            title = "Eco-AI Coach",
            subtitle = "Contextual Planetary Intelligence",
            actions = {
                if (uiState.messages.isNotEmpty()) {
                    IconButton(onClick = { showClearHistoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = "Clear Conversation History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )

        // Error Banner
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CoralAlert.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CoralAlert.copy(alpha = 0.40f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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

        // Conversational Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isTyping) {
                item {
                    EmptyChatGreeting()
                }
            }

            items(uiState.messages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    onDelete = { onDeleteMessage(message.id) }
                )
            }

            if (uiState.isTyping) {
                item {
                    TypingIndicatorBubble()
                }
            }
        }

        // Horizontal Quick-Prompt Suggestion Chips
        if (uiState.quickChips.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.quickChips) { chipText ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BioLuminescentEmerald.copy(alpha = 0.30f)),
                        modifier = Modifier.clickable { onQuickPromptSelected(chipText) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = BioLuminescentEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = chipText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Natural Language Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputPrompt,
                    onValueChange = onInputPromptChanged,
                    placeholder = { Text("Ask your eco coach anything...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BioLuminescentEmerald,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        cursorColor = BioLuminescentEmerald
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val trimmed = uiState.inputPrompt.trim()
                        if (trimmed.isNotBlank()) {
                            onSendMessage(trimmed)
                        }
                    },
                    enabled = uiState.isSendEnabled
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        tint = if (uiState.isSendEnabled) BioLuminescentEmerald else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }

    // Confirmation Dialog for Clearing Conversation History
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            containerColor = DeepForestObsidian,
            title = {
                Text(
                    text = "Clear Conversation?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "This will remove all logged coach advice and user prompts from your local memory ledger.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(
                        text = "Clear History",
                        color = CoralAlert,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Composable
private fun EmptyChatGreeting() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = BioLuminescentEmerald.copy(alpha = 0.12f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = BioLuminescentEmerald,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Welcome to Eco-AI Coach",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Ask questions regarding carbon emissions, circular zero-waste swaps, or get guidance tailored to your active quests.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    onDelete: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) BioLuminescentEmerald.copy(alpha = 0.18f) else DeepForestObsidian,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) BioLuminescentEmerald.copy(alpha = 0.45f) else SolarCyan.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isUser) Icons.Filled.EnergySavingsLeaf else Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = if (isUser) BioLuminescentEmerald else SolarCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUser) "You" else "Eco-AI Coach",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isUser) BioLuminescentEmerald else SolarCyan
                        )
                    }

                    message.suggestedActionCategory?.let { category ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SolarCyan.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, SolarCyan.copy(alpha = 0.30f))
                        ) {
                            Text(
                                text = "${category.emoji} ${category.displayName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = SolarCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun TypingIndicatorBubble() {
    val transition = rememberInfiniteTransition(label = "TypingTransition")
    val dotAlpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotPulse"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DeepForestObsidian,
            border = androidx.compose.foundation.BorderStroke(1.dp, SolarCyan.copy(alpha = 0.35f)),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = SolarCyan,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Coach is formulating advice",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(dotAlpha)
                        .background(BioLuminescentEmerald, CircleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF081410)
@Composable
private fun AiAssistantScreenPreview() {
    Change360Theme(darkTheme = true) {
        AiAssistantContent(
            uiState = AiAssistantUiState(
                quickChips = listOf("Calculate commute footprint", "5-min zero-waste swap")
            ),
            onInputPromptChanged = {},
            onSendMessage = {},
            onQuickPromptSelected = {},
            onClearHistory = {},
            onDeleteMessage = {},
            onClearError = {}
        )
    }
}

