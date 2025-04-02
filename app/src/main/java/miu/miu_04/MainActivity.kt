package miu.miu_04

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import miu.miu_04.data.CallRecord
import miu.miu_04.data.CallRecord2
import miu.miu_04.ui.theme.AppTheme
import miu.miu_04.viewmodel.CallViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDarkTheme
        }

        setContent {
            AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneRecordSearchScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneRecordSearchScreen(viewModel: CallViewModel = viewModel()) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedDataSet by remember { mutableStateOf(0) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val maxPhoneNumberLength = 9

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Informacje o aplikacji") },
            text = {
                Column {
                    Text(
                        "Aplikacja umożliwia sprawdzenie listy połączeń z wybranej bazy połączeń.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Jak korzystać z wyszukiwarki:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Wprowadź numer telefonu, który chcesz wyszukać. Obsługiwane są wyłącznie polskie numery telefonów komórkowych. Numer musi być wprowadzony w formacie 9 cyfr, bez kodu kraju.")
                    Text("2. Wybierz bazę połączeń, z której chcesz wyszukać połączenia. Dostępne są dwa źródła danych.")
                    Text("3. Kliknij przycisk 'Wyszukaj'")
                    Text("4. Wyniki wyszukiwania zostaną wyświetlone poniżej. W przypadku braku wyników zostanie wyświetlony komunikat.")
                    Text("Wszelkie błędy związane (brak połączenia itd.) z bazą zostaną wyświetlone w polu wyników.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Telefon") },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informacje o aplikacji"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Wyszukaj połączenia telefoniczne",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    val digitsOnly = newValue.filter { it.isDigit() }
                    if (digitsOnly.length <= maxPhoneNumberLength) {
                        phoneNumber = digitsOnly
                    }
                },
                label = { Text("Numer telefonu") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text("Wybierz bazę połączeń:", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDataSet = 0 }
                    ) {
                        RadioButton(
                            selected = selectedDataSet == 0,
                            onClick = { selectedDataSet = 0 }
                        )
                        Text(
                            text = "Baza 1",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDataSet = 1 }
                    ) {
                        RadioButton(
                            selected = selectedDataSet == 1,
                            onClick = { selectedDataSet = 1 }
                        )
                        Text(
                            text = "Baza 2",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            TextButton(
                onClick = { viewModel.addSampleData() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Dodaj przykładowe dane")
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.searchCalls(phoneNumber, selectedDataSet)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wyszukaj")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (searchResults.isEmpty()) {
                    Text(
                        text = "Brak wyników",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn {
                        items(searchResults) { record ->
                            CallRecordItem(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CallRecordItem(record: Any) {
    val phoneNumber: String
    val startDate: Date
    val endDate: Date

    when (record) {
        is CallRecord -> {
            phoneNumber = record.phoneNumber
            startDate = record.startDate
            endDate = record.endDate
        }
        is CallRecord2 -> {
            phoneNumber = record.phoneNumber
            startDate = record.startDate
            endDate = record.endDate
        }
        else -> return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Numer: $phoneNumber")
            Text("Rozpoczęcie: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(startDate)}")
            Text("Zakończenie: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(endDate)}")

            val callDuration = (endDate.time - startDate.time) / 1000
            Text("Czas trwania: ${callDuration}s")
        }
    }
}