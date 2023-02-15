package me.gentilpinto.unscramble.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.gentilpinto.unscramble.R
import me.gentilpinto.unscramble.ui.theme.UnscrambleTheme

@Preview(
    name = "Light Mode",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@ExperimentalMaterial3Api
@Composable
fun GameScreenPreview() {
    UnscrambleTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            GameScreen()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel(),
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GameStatus(
            wordCount = gameUiState.currentWordCount,
            score = gameUiState.score,
        )
        GameLayout(
            userGuess = gameViewModel.userGuess,
            isGuessWrong = gameUiState.isGuessedWordWrong,
            onUserGuessChanged = {
                gameViewModel.updateUserGuess(it)
            },
            onKeyboarDone = {
                gameViewModel.checkUserGuess()
            },
            currentScrambledWord = gameUiState.currentScrambledWord,
        )
        ActionButtons(
            onSubmit = {
                gameViewModel.checkUserGuess()
            },
            skipWord = {
                gameViewModel.skipWord()
            },
        )
    }
    if (gameUiState.isGameOver) {
        FinalScoreDialog(
            score = gameUiState.score,
            onPlayAgain = { gameViewModel.resetGame() },
        )
    }
}

@Composable
fun GameStatus(
    wordCount: Int, score: Int, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(48.dp),
    ) {
        Text(
            text = stringResource(R.string.word_count, wordCount),
            fontSize = 18.sp,
        )
        Text(
            text = stringResource(R.string.score, score),
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun GameLayout(
    userGuess: String,
    isGuessWrong: Boolean,
    currentScrambledWord: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboarDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier,
    ) {
        Text(
            text = currentScrambledWord,
            fontSize = 45.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(R.string.instructions),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        OutlinedTextField(
            value = userGuess,
            singleLine = true,
            onValueChange = onUserGuessChanged,
            label = {
                when (isGuessWrong) {
                    true -> Text(stringResource(R.string.wrong_guess))
                    false -> Text(stringResource(R.string.enter_your_word))
                }
            },
            isError = isGuessWrong,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                onKeyboarDone()
            }),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ActionButtons(
    onSubmit: () -> Unit,
    skipWord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        OutlinedButton(
            onClick = skipWord,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Text(stringResource(R.string.skip))
        }
        Button(
            onClick = onSubmit,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        ) {
            Text(stringResource(R.string.submit))
        }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activityContext = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(stringResource(R.string.congratulations))
        },
        text = {
            Text(stringResource(R.string.you_scored, score))
        },
        shape = MaterialTheme.shapes.medium,
        dismissButton = {
            TextButton(onClick = {
                activityContext.finish()
            }) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onPlayAgain()
            }) {
                Text(text = stringResource(R.string.play_again))
            }
        },
        modifier = modifier,
    )
}