package ec.edu.uce.book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uce.book.data.database.AppDatabase
import ec.edu.uce.book.data.remote.DynamoDBHelper
import ec.edu.uce.book.data.remote.S3Helper
import ec.edu.uce.book.data.repository.ProductRepository
import ec.edu.uce.book.data.repository.UserRepository
import ec.edu.uce.book.ui.theme.BookTheme
import ec.edu.uce.book.util.NetworkCallback
import ec.edu.uce.book.util.SessionManager
import ec.edu.uce.book.util.SyncService
import ec.edu.uce.book.view.*
import ec.edu.uce.book.viewmodel.ProductViewModel
import ec.edu.uce.book.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var networkCallback: NetworkCallback
    private lateinit var syncService: SyncService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // DB
        val db = AppDatabase.getDatabase(applicationContext)

        // Dynamo
        val dynamoDBHelper = DynamoDBHelper(applicationContext)

        // Repos
        val productRepository = ProductRepository(
            productDao = db.productDao(),
            context = applicationContext,
            dynamoDBHelper = dynamoDBHelper,
            s3Helper = S3Helper(applicationContext)

        )
        val userRepository = UserRepository(db.userDao())


        syncService = SyncService(
            context = applicationContext,
            productRepository = productRepository,
            productDao = db.productDao(),
            dynamoDBHelper = dynamoDBHelper
        )

        // Network monitor
        networkCallback = NetworkCallback(applicationContext)
        networkCallback.startMonitoring()

        // Auto-sync cuando vuelve el internet
        lifecycleScope.launch {
            var wasOffline = false
            networkCallback.isOnline.collect { isOnline ->
                if (isOnline && wasOffline) {
                    syncService.performFullSync()
                }
                wasOffline = !isOnline
            }
        }

        // Factory ProductViewModel
        val productViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ProductViewModel(application, productRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // Factory UserViewModel
        val userViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            BookTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        productViewModelFactory = productViewModelFactory,
                        userViewModelFactory = userViewModelFactory,
                        syncService = syncService,
                        networkCallback = networkCallback
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback.stopMonitoring()
    }
}

@Composable
fun AppNavigation(
    productViewModelFactory: ViewModelProvider.Factory,
    userViewModelFactory: ViewModelProvider.Factory,
    syncService: SyncService,
    networkCallback: NetworkCallback
) {
    val productViewModel: ProductViewModel = viewModel(factory = productViewModelFactory)
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)

    var currentScreen by remember { mutableStateOf("welcome") }
    var selectedProduct by remember { mutableStateOf<ec.edu.uce.book.data.entity.ProductEntity?>(null) }

    // ✅ scope correcto para lanzar coroutines desde Compose
    val scope = rememberCoroutineScope()

    // Sesión
    val isSessionExpired by SessionManager.isSessionExpired.collectAsState()
    var showSessionExpiredDialog by remember { mutableStateOf(false) }

    // Red
    val isOnline by networkCallback.isOnline.collectAsState()

    // Detectar expiración
    LaunchedEffect(isSessionExpired) {
        if (isSessionExpired && currentScreen !in listOf("welcome", "login", "register")) {
            showSessionExpiredDialog = true
        }
    }

    // Actualizar actividad (para sesión)
    LaunchedEffect(currentScreen) {
        if (currentScreen !in listOf("welcome", "login", "register")) {
            SessionManager.updateActivity()
        }
    }

    if (showSessionExpiredDialog) {
        AlertDialog(
            onDismissRequest = { /* no cerrar tocando afuera */ },
            title = { Text("Sesión Expirada") },
            text = { Text("Tu sesión ha expirado por inactividad o tiempo máximo. Por favor, inicia sesión nuevamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSessionExpiredDialog = false
                    SessionManager.endSession()
                    currentScreen = "login"
                }) { Text("Aceptar") }
            }
        )
    }

    when (currentScreen) {

        "welcome" -> WelcomeScreen {
            currentScreen = "login"
        }

        "login" -> LoginScreen(
            userViewModel = userViewModel,
            onLoginSuccess = {
                SessionManager.startSession()

                // ✅ Sincronizar al iniciar sesión (sin GlobalScope)
                if (isOnline) {
                    scope.launch { syncService.performFullSync() }
                }

                currentScreen = "home"
            },
            onRegister = { currentScreen = "register" }
        )

        "register" -> RegisterScreen(
            userViewModel = userViewModel,
            onRegisterSuccess = { currentScreen = "login" },
            onBackToLogin = { currentScreen = "login" }
        )

        "home" ->HomeScreen(
            productViewModel = productViewModel,
            isOnline = isOnline,
            onLogout = {
                SessionManager.endSession()
                currentScreen = "login"
            },
            onAddProduct = {
                SessionManager.updateActivity()
                selectedProduct = null
                currentScreen = "form"
            },
            onEditProduct = { product ->
                SessionManager.updateActivity()
                selectedProduct = product
                currentScreen = "form"
            }
        )


        "form" -> ProductFormScreen(
            productViewModel = productViewModel,
            productToEdit = selectedProduct,
            onFinish = {
                SessionManager.updateActivity()
                currentScreen = "home"
            }
        )
    }
}
