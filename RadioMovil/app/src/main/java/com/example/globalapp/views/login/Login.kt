package com.example.globalapp.views.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.globalapp.R
import com.example.globalapp.components.MyFloatingActionButton
import com.example.globalapp.components.OunTextField
import com.example.globalapp.components.OunTextFieldPassword
import com.example.globalapp.util.StoreLogin
import com.example.globalapp.views.login.controllers.AuthPurpose
import com.example.globalapp.views.login.controllers.ControllerLogin

@Composable
fun LoginNav(navController: NavController,viewModelLogin: ControllerLogin){
    Login(navController,viewModelLogin)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    navController: NavController,viewModelLogin: ControllerLogin
){
    val context = LocalContext.current
    val storeLogin = StoreLogin(context);

    viewModelLogin.setUserLogin(storeLogin.getUserStore.collectAsState(initial = "").value);
    var userStore = storeLogin.getUserStore.collectAsState(initial = "").value;
    var passwordStore = storeLogin.getPasswordStore.collectAsState(initial = "").value;
    LaunchedEffect(viewModelLogin.controllerAuthFingerprint.isAutenticate,viewModelLogin.controllerAuthFingerprint.authPurpose) {
        if (viewModelLogin.controllerAuthFingerprint.isAutenticate) {
            when(viewModelLogin.controllerAuthFingerprint.authPurpose){
                AuthPurpose.LOGIN -> {
                    Log.i("autentication:login","ENTRO")
                    viewModelLogin.handlerEntrar(navController, viewModelLogin.user, passwordStore, storeLogin)
                }
                else -> Unit
            }

        }
    }

    var border = Modifier.border(1.dp,Color.Gray)
    Scaffold (
        floatingActionButton = {
            MyFloatingActionButton(
                onClick = { viewModelLogin.handlerFingerPrint(navController,userStore, AuthPurpose.LOGIN) },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                enabled = !viewModelLogin.isLoading
            ) {
                Icon(
                    painterResource(id = R.drawable.baseline_fingerprint_24),
                    "Localized description",
                    modifier = border.size(46.dp))
            }
        }
    ){ innerPadding ->
//        Column(
        LazyColumn(
            modifier = Modifier
//                .padding(innerPadding)
                .fillMaxSize()
               // .padding
                .padding(innerPadding)
                .padding(top =16.dp, start = 16.dp, end = 16.dp)
//                .padding(16.dp)
                .imePadding(),
//                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.logo_radioformula1),
                    contentDescription = "logo",
                    modifier =
                    Modifier
//                        .size(200.dp)
                        .width(300.dp)
                        .height(100.dp)
                        .clip(CircleShape)//HACE QUE SEA CIRCULAR
    //                    .border(BorderStroke(1.dp,Color.Gray), shape = RoundedCornerShape(100.dp))
//                        .background(colorResource(R.color.set_tertiary))
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, colorResource(R.color.set_secundary), shape = RoundedCornerShape(20.dp))
                        .clip(shape = RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.background(color = colorResource(id = R.color.opaque))
                            .border(1.dp, Color.Gray)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LOGIN",
                            modifier = Modifier
                                .padding(4.dp),
                            textAlign = TextAlign.Center,
                            color = colorResource(R.color.set_secundary),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        OunTextField(
                            viewModelLogin.user,
                            "Usuario",
                            enabled = !viewModelLogin.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            viewModelLogin.onValue(it, "user")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OunTextFieldPassword(
                            viewModelLogin.password,
                            "Contraseña",
                            enabled = !viewModelLogin.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            viewModelLogin.onValue(it, "password")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModelLogin.handlerEntrar(
                                    navController,
                                    viewModelLogin.user.trim(),
                                    viewModelLogin.password.trim(),
                                    storeLogin
                                );
                            },
                            enabled = !viewModelLogin.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = colorResource(R.color.set_secundary),
                            )
//                        colors = ButtonColors(
//                            contentColor = Color.White,
//                            containerColor = Color.Gray,
//                            //disabledContentColor = Color.Red,
//                            //disabledContainerColor = Color.Black
//                        )
                        ) {
                            Text("Entrar")
                        }

                        if (viewModelLogin.isLoading) {
                            Spacer(Modifier.height(10.dp))
                            CircularProgressIndicator()
                        }
                        if (viewModelLogin.loginResponse.MESSAGE != "") {
                            Spacer(Modifier.height(10.dp))
                            Image(
                                painter = painterResource(id = R.drawable.baseline_warning_24),
                                contentDescription = "logo",
                                modifier =
                                Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)//HACE QUE SEA CIRCULAR
                                //.background(Color.Gray)
                            )
                            Text(viewModelLogin.loginResponse.MESSAGE, color = Color.Red)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
