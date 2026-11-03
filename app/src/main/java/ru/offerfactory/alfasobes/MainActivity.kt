package ru.offerfactory.alfasobes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.offerfactory.alfasobes.presentation.RegistrationScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.offerfactory.alfasobes.presentation.RegistrationViewModel
import ru.offerfactory.alfasobes.ui.theme.AlfasobesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlfasobesTheme {
                val viewModel: RegistrationViewModel = viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                RegistrationScreen(state, onAction = viewModel::onAction)
            }
        }
    }
}
