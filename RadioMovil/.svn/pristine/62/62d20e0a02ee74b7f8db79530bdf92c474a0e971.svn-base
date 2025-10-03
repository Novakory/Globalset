package com.example.globalapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalapp.R
import com.example.globalapp.components.GSwitch
import com.example.globalapp.components.OunTextField
import com.example.globalapp.components.TextAlert
import com.example.globalapp.util.Constants

@Preview(showBackground = true)
@Composable
fun lines(){
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text("ó", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
@Preview(showBackground = true)
@Composable
fun DialogViewPreviews() {
    var border = Modifier.border(1.dp, Color.Gray)
    var password by rememberSaveable { mutableStateOf("") };

    Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//                .then(border),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_radioformula1),
            contentDescription = "logo",
            modifier =
            Modifier
//                    border(1.dp,Color.Gray)
//                .size(100.dp)
                .width(200.dp)
                .height(100.dp)
                .clip(CircleShape)//HACE QUE SEA CIRCULAR
//                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))
                .clip(shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.background(color = colorResource(id = R.color.opaque))
                    .border(1.dp, Color.Gray)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

//                    OunTextField(user, onValueChange = {newText->user=newText},"Usuario", modifier = Modifier.fillMaxWidth())
//                    Spacer(modifier = Mo  difier.height(10.dp))
                OunTextField(password, "Contraseña", modifier = Modifier.fillMaxWidth()) {

                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
//                        viewoModelPropuestas.handlerAutorizar(
//                            viewModelLogin.idUsuario,
//                            viewModelLogin,
//                            viewModelLogin.user.trim(),
//                            password.trim()
//                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color.Gray,
                        disabledContentColor = Color.Red,
                        disabledContainerColor = Color.Black
                    )
                ) {
                    Text("Aceptar")
                }

                Spacer(Modifier.height(10.dp))
                TextAlert(
                    "No puedes desautorizar la propuesta M1420241203 ya que no eres el último autorizador",
                    Constants.ICO_WARNING,
                    textAlign =  TextAlign.Center
                )

//            if (viewoModelPropuestas.isLoading) {
                Spacer(Modifier.height(10.dp))
                CircularProgressIndicator()

//            }
                Text("Cargando...")


                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Text("ó", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                FloatingActionButton(
                    onClick = {
                        //handlerFingerPrint(navController)
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_fingerprint_24),
                        "Localized description",
                        modifier = border.size(46.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainHaderNav(){
    Row(Modifier.fillMaxWidth()
        .padding(horizontal = 8.dp)
        , horizontalArrangement = Arrangement.SpaceBetween){
        Text(
            "Propuestas",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterVertically)

        )
        HorizontalDivider(
            modifier = Modifier.weight(1f)
                .align(Alignment.CenterVertically)
        )
        val switch = false
        GSwitch("Pendientes",switch){}
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(){
//    val nav = rememberNavController();
//    val viewModel: ControllerLogin = viewModel();
//    LoginNav(nav,viewModel);
    LoginPrev()
}
//fecha propuesta
//concepto
//nivel autorizacion 2
//nivel autorizacion 3
//banco pago
//chequera pago
//listado de documentos
//beneficiario
//banco benef
//chequera benef
//id divisa
//forma de pago
//no docto
//importe


@Composable
fun LoginPrev(){
    var border = Modifier.border(1.dp,Color.Gray)
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    painterResource(id = R.drawable.baseline_fingerprint_24),
                    "Localized description",
                    modifier = border.size(46.dp))
            }
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_radioformula1),
                contentDescription = "logo",
                modifier =
                Modifier
//                    .size(300.dp)
                    .width(300.dp)
                    .height(100.dp)
                    .clip(CircleShape)//HACE QUE SEA CIRCULAR
//                    .border(BorderStroke(5.dp, colorResource(R.color.set_tertiary)), shape = RoundedCornerShape(300.dp))
                    .background(colorResource(R.color.opaque))
                    .padding(16.dp)
//                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Box (
                modifier = Modifier
                    .border(1.dp, colorResource(R.color.set_secundary), shape = RoundedCornerShape(20.dp))
                    .clip(shape = RoundedCornerShape(20.dp))
            ){
                Column (
                    modifier = Modifier.background(color = colorResource(id = R.color.opaque))
                        .border(1.dp,Color.Gray)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "LOGIN",
                        modifier = Modifier
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.set_secundary),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    val user=""
                    val password=""
                    OunTextField(user,"Usuario", modifier = Modifier.fillMaxWidth()){

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OunTextField(password,"Contraseña", modifier = Modifier.fillMaxWidth()){

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {

                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(R.color.set_secundary),
                            disabledContentColor = Color.Red,
                            disabledContainerColor = Color.Black
                        )
                    ) {
                        Text("Entrar")
                    }

                    Spacer(Modifier.height(10.dp))
                    CircularProgressIndicator()
                    Image(
                        painter = painterResource(id = R.drawable.baseline_warning_24),
                        contentDescription = "logo",
                        modifier =
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)//HACE QUE SEA CIRCULAR
                        //.background(Color.Gray)
                    )
                    Text("Credenciales incorrecas", color = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}