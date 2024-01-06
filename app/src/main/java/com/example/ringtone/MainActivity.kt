package com.example.ringtone

import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.example.ringtone.ui.theme.RingtoneTheme
import com.example.ringtone.R.string
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RingtoneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Greeting("Android")
                        LocaleSelector()
                        RingtoneManagerPicker()
                        RingtoneList()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = string.hello, name),
        modifier = modifier
    )
}

@Composable
fun LocaleSelector() {
    var currentLocale by remember {
        mutableStateOf(AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault())
    }
    val locales = remember {
        listOf(
            Locale("es"),
            Locale.ENGLISH,
            Locale.FRENCH,
        )
    }

    Column {
        Text(
            text = stringResource(id = string.current_locale, currentLocale.displayLanguage)
        )

        locales.forEach {
            val selected = it.displayLanguage == currentLocale.displayLanguage
            Row(
                modifier = Modifier.clickable {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(it))
                    currentLocale = it
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = {}
                )
                Text(text = it.getDisplayLanguage(it))
            }
        }
    }
}

@Composable
fun RingtoneManagerPicker() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            ContextCompat.startActivity(context, intent, null)
        },
        modifier = Modifier.defaultMinSize(minWidth = 150.dp),
    ) {
        Text(
            text = stringResource(id = string.ringtone_picker),
        )
    }
}

@Composable
fun RingtoneList() {
    val ringtoneList: MutableState<List<String>> = remember {
        mutableStateOf(emptyList())
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch {
            val ringtoneManager =
                RingtoneManager(ContextCompat.getContextForLanguage(context)).also {
                    it.setType(RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_RINGTONE)
                }
            val cursor = ringtoneManager.cursor
            val tmpList = mutableListOf<String>()
            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                tmpList.add(title)
            }
            ringtoneList.value = tmpList
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        items(
            count = ringtoneList.value.size,
        ) { index ->
            val item = ringtoneList.value[index]
            Text(text = item)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RingtoneTheme {
        Greeting("Android")
    }
}