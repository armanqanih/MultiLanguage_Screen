package com.example.twolanguge_application

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlinx.coroutines.launch
import java.util.Locale

private val language = listOf("English", "arabic")

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiLanguage(
    viewModel: LanguageViewModel = viewModel(
        factory = DataStoreViewModelFactory(DataStorePreferenceRepository(LocalContext.current))
    )
) {
    val scope = rememberCoroutineScope()
    val currentLanguage = viewModel.language.observeAsState().value
    val menuExpanded = remember { mutableStateOf(false) }

    SetLanguage(position = currentLanguage!!)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MultiLanguage",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { menuExpanded.value = true }) {

                    }


                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState(0))
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            LanguagePicker(currentLanguage) { selected ->
                scope.launch {
                    viewModel.saveLanguage(selected)
                }
            }
        }
    }
}

@Composable
fun LanguagePicker(
    selectedPosition: Int,
    onLanguageSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),

    ) {
        LanguageContentPortrait(selectedPosition, onLanguageSelected)
    }
}

@Composable
fun LanguageContentPortrait(
    selectedPosition: Int,
    onLanguageSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        ToggleGroup(selectedPosition = selectedPosition, onClick = onLanguageSelected)
        Spacer(modifier = Modifier.height(60.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.content),
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SetLanguage(position: Int) {
    val locale = Locale(if (position == 1) "ar" else "en")
    val configuration = LocalConfiguration.current
    configuration.setLocale(locale)
    val resources = LocalContext.current.resources
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

@Composable
private fun ToggleGroup(
    selectedPosition: Int,
    onClick: (Int) -> Unit
) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape)
            .border(1.dp, Color(0xFFAAAAAA), shape)
    ) {
        language.forEachIndexed { index, element ->
            val verticalPadding = if (index == selectedPosition) 8.dp else 0.dp
            Text(
                text = element,
                color = if (index != selectedPosition) Color.Black else Color.White,
                modifier = Modifier
                    .align(CenterVertically)
                    .run {
                        if (index != selectedPosition) this
                        else background(Color.Transparent).border(1.dp, Color.Gray)
                    }
                    .clickable(
                        onClick = { onClick(index) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp, vertical = verticalPadding)
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DataStoreViewModelFactory(private val dataStorePreferenceRepository: DataStorePreferenceRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
            return LanguageViewModel(dataStorePreferenceRepository) as T
        }
        throw IllegalStateException()
    }
}