package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.SportProAccentGold
import com.example.ui.theme.SportProNavy
import com.example.ui.theme.SportProPitchGreen
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("carlos.dt@sportpro.com") }
    var password by remember { mutableStateOf("SportPro2026*") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val lockoutSeconds by viewModel.repository.lockoutSeconds.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var lockoutCountdown by remember { mutableStateOf(lockoutSeconds) }

    LaunchedEffect(lockoutSeconds) {
        lockoutCountdown = lockoutSeconds
        while (lockoutCountdown > 0) {
            delay(1000)
            lockoutCountdown -= 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top row with Theme toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistChip(
                onClick = { viewModel.toggleTheme() },
                label = {
                    Text(
                        text = if (isDarkTheme) "Fondo Negro" else "Fondo Blanco",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = if (isDarkTheme) SportProAccentGold else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // App Logo & Brand
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SportProPitchGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SportPro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Gestión integral de academias de fútbol",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Demo Quick Switcher Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Acceso Rápido Prototipo (Selecciona Rol)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserRole.values().forEach { role ->
                        OutlinedButton(
                            onClick = {
                                viewModel.switchRole(role)
                                viewModel.navigateTo(Screen.HOME)
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = role.code, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                errorMessage = null
            },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@sportpro.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password Field with Visibility Toggle (US-002)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                errorMessage = null
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Alternar visibilidad"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(12.dp)
        )

        // General Error message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Lockout message after 3 failed attempts (US-002)
        if (lockoutCountdown > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Demasiados intentos fallidos. Inténtalo en $lockoutCountdown s",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { showForgotPasswordDialog = true }) {
                Text(text = "¿Olvidaste tu contraseña?", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Login Button
        Button(
            onClick = {
                if (email.isBlank()) {
                    emailError = "Campo obligatorio"
                    return@Button
                }
                if (password.isBlank()) {
                    passwordError = "Campo obligatorio"
                    return@Button
                }
                val res = viewModel.repository.login(email, password)
                if (res.isSuccess) {
                    viewModel.navigateTo(Screen.HOME)
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            },
            enabled = lockoutCountdown <= 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "¿Aún no tienes cuenta?", fontSize = 13.sp)
            TextButton(onClick = { viewModel.navigateTo(Screen.REGISTER) }) {
                Text(text = "Registrarme (US-001)", fontWeight = FontWeight.Bold)
            }
        }
    }

    // US-002: Dialog to recover password
    if (showForgotPasswordDialog) {
        var recoveryEmail by remember { mutableStateOf(email) }
        var recoverySent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Recuperar Contraseña") },
            text = {
                if (recoverySent) {
                    Column {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SportProPitchGreen, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Si el correo está registrado recibirás un enlace de recuperación.",
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column {
                        Text(
                            text = "Ingresa tu correo electrónico registrado para enviarte un enlace de restablecimiento.",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = recoveryEmail,
                            onValueChange = { recoveryEmail = it },
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (!recoverySent) {
                    Button(
                        onClick = { recoverySent = true }
                    ) {
                        Text("Enviar Enlace")
                    }
                } else {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Entendido")
                    }
                }
            },
            dismissButton = {
                if (!recoverySent) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("2010-05-18") }
    var telefono by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.JUG) }
    var correoApoderado by remember { mutableStateOf("") }
    var invitationCode by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf<String?>(null) }

    // Validation state
    var nombresError by remember { mutableStateOf<String?>(null) }
    var apellidosError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var apoderadoError by remember { mutableStateOf<String?>(null) }
    var invitationCodeError by remember { mutableStateOf<String?>(null) }

    // Age calculation for minor rule (US-001)
    val isMinor = remember(fechaNacimiento, selectedRole) {
        if (selectedRole != UserRole.JUG) false
        else {
            try {
                val year = fechaNacimiento.split("-")[0].toIntOrNull() ?: 2008
                (2026 - year) < 18
            } catch (e: Exception) {
                false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta en SportPro",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "US-001: Registro con selección de rol y reglas de privacidad",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Nombres Field (2-50 chars, alphabetic only)
        OutlinedTextField(
            value = nombres,
            onValueChange = {
                nombres = it
                nombresError = when {
                    it.length < 2 -> "Mínimo 2 caracteres"
                    it.length > 50 -> "Máximo 50 caracteres"
                    it.any { ch -> ch.isDigit() } -> "Solo se permiten letras"
                    else -> null
                }
            },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombresError != null,
            supportingText = { nombresError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Apellidos Field (2-50 chars, alphabetic only)
        OutlinedTextField(
            value = apellidos,
            onValueChange = {
                apellidos = it
                apellidosError = when {
                    it.length < 2 -> "Mínimo 2 caracteres"
                    it.length > 50 -> "Máximo 50 caracteres"
                    it.any { ch -> ch.isDigit() } -> "Solo se permiten letras"
                    else -> null
                }
            },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            isError = apellidosError != null,
            supportingText = { apellidosError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Correo Field (valid email format)
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                correoError = if (!it.contains("@") || !it.contains(".")) "Correo inválido" else null
            },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = correoError != null,
            supportingText = { correoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contraseña (min 8 chars, 1 uppercase, 1 digit, 1 special char)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                val hasUpper = it.any { c -> c.isUpperCase() }
                val hasDigit = it.any { c -> c.isDigit() }
                val hasSpecial = it.any { c -> !c.isLetterOrDigit() }
                passwordError = if (it.length < 8 || !hasUpper || !hasDigit || !hasSpecial) {
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un carácter especial"
                } else null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = if (it != password) "Las contraseñas no coinciden" else null
            },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmPasswordError != null,
            supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Fecha de Nacimiento
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de nacimiento (AAAA-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Teléfono
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono de contacto (9 dígitos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Selector de Rol
        Text(
            text = "Seleccione su rol en la academia *",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            UserRole.values().forEach { role ->
                FilterChip(
                    selected = selectedRole == role,
                    onClick = { selectedRole = role },
                    label = { Text(role.code, fontSize = 12.sp) }
                )
            }
        }

        // US-001: Minor rule for Jugador
        if (isMinor) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SportProAccentGold.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = SportProAccentGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cuenta de menor de edad detectada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SportProAccentGold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Conforme al criterio US-001 y políticas de privacidad, debes indicar el correo de tu apoderado (padre/tutor).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = correoApoderado,
                        onValueChange = {
                            correoApoderado = it
                            apoderadoError = if (!it.contains("@")) "Correo inválido" else null
                        },
                        label = { Text("Correo del apoderado *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = apoderadoError != null,
                        supportingText = { apoderadoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            }
        }

        // US-001: Admin invitation code rule
        if (selectedRole == UserRole.ADM) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SportProNavy.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Código de invitación de Administrador (8 caracteres)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = invitationCode,
                        onValueChange = {
                            invitationCode = it
                            invitationCodeError = if (it.length != 8) "Código de invitación inválido (debe tener 8 caracteres)" else null
                        },
                        placeholder = { Text("Ej: SP-AD881") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = invitationCodeError != null,
                        supportingText = { invitationCodeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Create Account Button with circular progress indicator
        Button(
            onClick = {
                if (nombres.isBlank() || apellidos.isBlank() || correo.isBlank() || password.isBlank()) {
                    generalError = "Por favor completa todos los campos obligatorios"
                    return@Button
                }
                if (isMinor && correoApoderado.isBlank()) {
                    apoderadoError = "Obligatorio para menores de edad"
                    return@Button
                }
                if (selectedRole == UserRole.ADM && invitationCode.length != 8) {
                    invitationCodeError = "Código de invitación inválido"
                    return@Button
                }

                isSubmitting = true
                val newUser = UserProfile(
                    uid = "usr_${System.currentTimeMillis()}",
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    telefono = telefono,
                    fechaNacimiento = fechaNacimiento,
                    rol = selectedRole,
                    esMenor = isMinor,
                    correoApoderado = if (isMinor) correoApoderado else null
                )
                viewModel.repository.registerUser(newUser)
                isSubmitting = false
                showSuccessDialog = true
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { viewModel.navigateTo(Screen.LOGIN) }) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.navigateTo(Screen.LOGIN)
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SportProPitchGreen, modifier = Modifier.size(48.dp)) },
            title = { Text("¡Cuenta Creada!") },
            text = {
                Text("Cuenta creada. Verifica tu correo para continuar con la activación de tu perfil en SportPro.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.navigateTo(Screen.LOGIN)
                    }
                ) {
                    Text("Ir al Inicio de Sesión")
                }
            }
        )
    }
}
