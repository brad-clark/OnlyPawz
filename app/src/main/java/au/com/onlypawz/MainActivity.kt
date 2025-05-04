package au.com.onlypawz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import au.com.onlypawz.theme.OnlyPawzTheme
import com.google.firebase.app
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
  private val pawzViewModel: PawzViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Firebase.initialize(context = this)
    Firebase.appCheck.installAppCheckProviderFactory(
      DebugAppCheckProviderFactory.getInstance(),
    )
    Log.e("FIREBASE", Firebase.app.options.apiKey)
    setContent {
      OnlyPawzTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          PawzScreen(pawzViewModel)
        }
      }
    }
  }
}