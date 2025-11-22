package com.example.globalapp.views.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.globalapp.R
import com.example.globalapp.components.GProgressBar
import com.example.globalapp.components.MyFloatingActionButton
import com.example.globalapp.components.OunTextField
import com.example.globalapp.components.OunTextFieldPassword
import com.example.globalapp.components.TextAlert
import com.example.globalapp.util.Constants
import com.example.globalapp.views.login.controllers.ControllerLogin
import com.example.globalapp.views.home.controllers.ControllerPropuestas
import com.example.globalapp.views.login.controllers.AuthPurpose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogRechazar(
    confirmText :String,
    navController: NavController,
    viewoModelPropuestas: ControllerPropuestas,
    viewModelLogin: ControllerLogin,
    onDismissRequest:()->Unit,
    onConfirmRequest: ()->Unit,
){
    AlertDialog(
        onDismissRequest,
        title = {
            Text(text = "RECHAZAR",
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,

                color = colorResource(R.color.set_secundary),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            DialogPanelRechazar(navController,viewoModelPropuestas,viewModelLogin)
        },

        confirmButton = {
            Button(
                onConfirmRequest,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = colorResource(R.color.set_secundary),
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewoModelPropuestas.controllerDialogRechazar.progressbarState.isLoading
            ) {
                Text(confirmText)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogPanelRechazar(
    navController: NavController,
    viewoModelPropuestas: ControllerPropuestas,
    viewModelLogin: ControllerLogin
) {
    val border = Modifier.border(1.dp, Color.Gray)
    val controllerDialogRechazar = viewoModelPropuestas.controllerDialogRechazar;

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_radioformula1),
            contentDescription = "logo",
            modifier =
            Modifier
//                .size(100.dp)
                .width(200.dp)
                .height(100.dp)
                .clip(CircleShape)//HACE QUE SEA CIRCULAR
//                    .border(BorderStroke(1.dp,Color.Gray), shape = RoundedCornerShape(100.dp))
//                .background(colorResource(R.color.set_tertiary))
                .padding(8.dp)
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
                OunTextField( controllerDialogRechazar.txtMotivoRechazo,"Motivo rechazo",
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = controllerDialogRechazar::updateTxtMotivoRechazo
                )
                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(10.dp))
                OunTextFieldPassword(controllerDialogRechazar.txtPasswordDialog, "Contraseña",
                    enabled = !controllerDialogRechazar.progressbarState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = controllerDialogRechazar::updateTxtPassword
                )
//                {
//                        viewoModelPropuestas.controllerDialogRechazar.updateTxtPassword(it)
//                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
//                        viewoModelPropuestas.handlerAutorizarDesautorizar(viewModelLogin.idUsuario,viewModelLogin,viewModelLogin.user.trim(),viewoModelPropuestas.controllerDialogAutorizarDesautorizar.txtPasswordDialog.toString())
//                        idUsuario,
//                        viewModelLogin,
                        viewoModelPropuestas.handlerRechazar(
                            viewModelLogin,
                            viewModelLogin.user.trim(),
                            controllerDialogRechazar.txtPasswordDialog,
                            controllerDialogRechazar.txtMotivoRechazo)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !controllerDialogRechazar.progressbarState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(R.color.set_secundary),
                    ),
//                    colors = ButtonColors(
//                        contentColor = Color.White,
//                        containerColor = Color.Gray,
//                        disabledContentColor = Color.Red,
//                        disabledContainerColor = Color.Black
//                    )
                ) {
                    Text("Aceptar")
                }
                if (controllerDialogRechazar.progressbarState.isLoading) {
                    Spacer(Modifier.height(10.dp))
                    GProgressBar(controllerDialogRechazar.progressbarState.message)
                }
                if(controllerDialogRechazar.responseService.SUCCESS == false) {
                    Spacer(Modifier.height(10.dp))
                    TextAlert(
                        controllerDialogRechazar.responseService.MESSAGE,
                        Constants.ICO_WARNING,
                        textAlign =  TextAlign.Center
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text("ó", fontSize = 24.sp//, fontWeight = FontWeight.Bold
                    , modifier = Modifier.padding(horizontal = 3.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }


                Spacer(modifier = Modifier.height(8.dp))

                MyFloatingActionButton(
                    enabled = !controllerDialogRechazar.progressbarState.isLoading,
                    onClick = {
                        viewModelLogin.handlerFingerPrint(navController,viewModelLogin.user,
                            AuthPurpose.RECHAZAR_PROPUESTAS)
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