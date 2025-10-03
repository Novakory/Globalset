package com.example.globalapp.views

import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.globalapp.navegation.AppScreens
import com.example.globalapp.R
import com.example.globalapp.components.GProgressBar
import com.example.globalapp.components.GSwitch
import com.example.globalapp.components.OunTextField
import com.example.globalapp.components.SimpleTextField
import com.example.globalapp.models.HeaderData
import com.example.globalapp.models.retrofit.WebSocketRequest
import com.example.globalapp.retrofit.WebSocketClient
import com.example.globalapp.retrofit.WebSocketClient2
import com.example.globalapp.retrofit.openWebSocket
import com.example.globalapp.retrofit.openWebSocket2
import com.example.globalapp.util.Constants
import com.example.globalapp.util.StoreLogin
import com.example.globalapp.util.dtoToGson
import com.example.globalapp.util.formatNumber
import com.example.globalapp.viewModels.ControllerDetallePropuesta
import com.example.globalapp.viewModels.ControllerLogin
import com.example.globalapp.viewModels.ControllerPropuestas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNav(navController: NavController,viewModel:ControllerPropuestas,viewModelLogin:ControllerLogin,viewModelDetallePropuesta: ControllerDetallePropuesta) {

    var border = Modifier.border(1.dp, Color.Gray)

    val context = LocalContext.current
    val storeLogin = StoreLogin(context);
//    var userStore = storeLogin.getUserStore.collectAsState(initial = "").value;
    var passwordStore = storeLogin.getPasswordStore.collectAsState(initial = "").value;

//    var webSocket: WebSocket? = null;
//    var clientWs:OkHttpClient? = null;
//    var webSocketClient2: WebSocketClient? = null;
    var webSocketClient2 by remember { mutableStateOf<WebSocketClient?>(null) }
    LaunchedEffect(Unit){
        webSocketClient2 = openWebSocket(viewModelLogin,viewModel,viewModelDetallePropuesta)
//        clientWs = openWebSocket(viewModel)
//        webSocket = openWebSocket(viewModel,viewModelDetallePropuesta)
//        val webSocletCloent2 = WebSocketClient2.getInstance(viewModel, viewModelDetallePropuesta)
//        webSocletCloent2.
//        webSocket = WebSocketClient2.currentWebSocket;
        viewModel.handlerGetPropuestas(viewModelLogin.user)
    }
    LaunchedEffect(viewModelLogin.isAutenticate) {
        if (viewModelLogin.isAutenticate) {
            Log.i("autentication:MainNav","ENTRO")
            viewModel.handlerAutorizarDesautorizar(viewModelLogin.idUsuario,viewModelLogin, viewModelLogin.user.toString(),passwordStore.toString())
        }
    }
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if(viewModel.tiempoInicioSuspendido !== 0L) {
                        Log.i("MainNav:ON_START", "onStart: ${Date().time}")
                        val tiempoSuspendido = Date().time - viewModel.tiempoInicioSuspendido
                        Log.i("MainNav:ON_START", "tiempo transcurrido: ${tiempoSuspendido}")
                        val isLogout = viewModel.validaTiempoSuspendido(tiempoSuspendido)
                        viewModel.updateTiempoInicioSuspendido(0)

                        if(isLogout){
                                mainLogout(webSocketClient2, navController)
                        }
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.i("MainNav:ON_STOP","onStop: ${Date().time}")
                    viewModel.updateTiempoInicioSuspendido(Date().time)
                }

//                Lifecycle.Event.ON_DESTROY -> {
//                    Log.i("Main:ON_DESTROY", "Pantalla eliminada de la pila")
//                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
//            webSocketClient2?.closeConnection()
            lifecycle.removeObserver(observer)
            Log.i("MainNav:onDispose","Destroy observer")
            viewModel.updateTiempoInicioSuspendido(0)
        }
    }
    val listData by viewModel.lista.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.set_secundary),
                    titleContentColor = Color.LightGray,
                ),
                title = {
                    Box{
                        Text("Principal")
                        Box(
                            Modifier
                                .offset(y = 22.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_account_circle_24),
                                    contentDescription = "Logout",
                                    tint = Color.LightGray,

                                    modifier = Modifier//.border(1.dp,Color.Black
                                        .size(18.dp,18.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                Text(
                                    viewModelLogin.user,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row{
                        Box(Modifier.padding(end = 10.dp)){
                            BadgedBox (
                                badge = {
                                    if(viewModel.numPropuestasAfectadas>0)
                                        Badge(){
                                            Text(viewModel.numPropuestasAfectadas.toString())
                                        }
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.handlerGetPropuestas(viewModelLogin.user)
                                    },
                                    modifier = Modifier//.border(1.dp,Color.LightGray)
                                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))

                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_update_24),
                                        contentDescription = "actualizar propuestas",
                                        tint = Color.LightGray,
                                    )
                                }
                            }
                        }

                        Column(){
                            IconButton(
                                onClick = {
//                                    CoroutineScope(Dispatchers.Main).launch {
//                                        Log.i("Main:logout","Cerrar session aqui 2")
//                                        if(webSocketClient2===null){
//                                            Log.i("Main:logout","Cerrar session aqui 2.1")
//                                        }else {
//                                            webSocketClient2?.closeConnection()
//                                        }
//                                    }
//                                    LaunchedEffect(Unit) {
//                                        withContext(Dispatchers.Main) {
//                                            webSocketClient2?.closeConnection()
//                                        }
//                                    }
                                    mainLogout(webSocketClient2,navController)
                                        //para eliminar las vistas detras, o especificar que esta es la primera vista y no pueda retroceder
                                        //WARNING - wl inclusive=true es para que destruya las pantallas , sino no se activa el evento del ciclo ON_DESTROY
                                },
                                modifier = Modifier//.border(1.dp,Color.LightGray)
                                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))
                                    .align(Alignment.CenterHorizontally)
                                    .width(105.dp)

                            ) {
                                Column{
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_logout_24),
                                        contentDescription = "Logout",
                                        tint = Color.LightGray,
                                        modifier = Modifier//.border(1.dp,Color.Black)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Text("Cerrar sesión",
                                        color = Color.LightGray)
                                }
                            }
                        }
                    }

                }
            )
        },
        floatingActionButton = {
            Column(modifier = Modifier
            ){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    // .border(BorderStroke(1.dp, Color.Black))
                ){


                    Box(
                        modifier = Modifier
                            //.fillMaxHeight()
                            //.width(100.dp)
                            .height(55.dp)
                            .weight(1f)
//                            .offset(x = 15.dp)
                            // .border(BorderStroke(1.dp, Color.Gray))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(colorResource(R.color.set_tertiary))
                            .padding(end = 15.dp)
                        //.background(Color.LightGray)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(Alignment.CenterVertically),
//                            horizontalArrangement = Arrangement.Center
                            //horizontalArrangement = Arrangement.SpaceEvenly

                        ){
//                            val count = listData.fold(0) { acum, propuesta ->//ES EL reduce pero con valor inicial
//                                if (propuesta.checked) acum + 1 else acum
//                            }
                            Text("(${
                                listData.fold(0) { acum, propuesta ->//ES EL reduce pero con valor inicial
                                    if (propuesta.checked) acum + 1 else acum
                                }
                            })",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .width(50.dp)
                            )
                            Text("Total:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                            )
                            Text(
                                formatNumber(
                                    listData.fold(0.0) { acum, propuesta ->//ES EL reduce pero con valor inicial
                                        if (propuesta.checked) acum + propuesta.importe else acum
                                    }
                                ),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier
                            )
                        }


                    }

                    Box(
                        modifier = Modifier
                            //.border(BorderStroke(1.dp, Color.Blue))
                            //.padding(end = 8.dp)//.width()
                            .offset(x = -15.dp)
                    ){
                        FloatingActionButton(
                            onClick = {
                                if(listData.fold(0) { acum, propuesta ->//ES EL reduce pero con valor inicial
                                        if (propuesta.checked) acum + 1 else acum }==0){
                                    Toast.makeText(navController.context,"Selecciona al menos una propuesta",Toast.LENGTH_SHORT);
                                }else{
                                    viewModel.updateShowDialog(true)
                                }
                            },
//                            containerColor = Color.LightGray,
                            containerColor = colorResource(R.color.set_primary3),
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(
                                painterResource(id = R.drawable.baseline_fingerprint_24),
                                "Localized description",
                                modifier = Modifier
                                    .size(46.dp)
                                    .padding(bottom = 10.dp)
                            )
                            Text(
                                text = if(viewModel.switchAutorizando) "Autorizar" else "Desautorizar",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 33.dp)
                            )

                        }
                    }
                }

            }

        },
        floatingActionButtonPosition = FabPosition.Center,
//        floatingActionButton = {
//            Column(modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp,Color.Red))){
//                Box(
//                    modifier = Modifier.weight(1f).border(BorderStroke(1.dp,Color.Blue))
//                ){
//
//                }
//
//                Box(
//                    modifier = Modifier.border(BorderStroke(1.dp,Color.Blue))//.width()
//                ){
//                    FloatingActionButton(
//                        onClick = {
//                            showDialog = true;
////                    handlerFingerPrint(navController)
//                        },
////                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
//                        containerColor = Color.LightGray,
//                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
//                    ) {
////                Icon(Icons.Filled.Add, "Localized description")
//
//                        Icon(
//                            painterResource(id = R.drawable.baseline_fingerprint_24),
//                            "Localized description",
//                            modifier = Modifier
//                                .size(46.dp)
//                                .padding(bottom = 10.dp)
//                        )
//                        Text(
//                            "Autorizar",
//                            fontSize = 10.sp,
////                    color = Color.w
//                            modifier = Modifier.padding(top = 33.dp)
//                        )
//
//                    }
//                }
//            }
//
//        },
//        floatingActionButtonPosition = FabPosition.Start,
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(8.dp)
                .then(border),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                GSwitch("Autorizar"
                    ,viewModel.switchAutorizando,
                    modifier = Modifier.weight(1f)){
                    viewModel.updateSwitchAutorizando(it)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp, end = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
//                    SimpleTextField(search,"", enabled = true, modifier = Modifier.fillMaxWidth(1f)) {}
                    SimpleTextField(viewModel.search,"", enabled = viewModel.isSearching.isNotBlank(), modifier = Modifier.fillMaxWidth(1f)) {
                        viewModel.onValue(it,"search")
                        viewModel.filterSearch()
                    }
                }
            }
            Row(Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp)
//                .then(border)
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

                GSwitch("Pendientes"
                    ,viewModel.switchPropuestasPendientes){
                    viewModel.updateSwitchPropuestasPendientes(it,viewModelLogin.user)
                }
            }


            //BODY PROPUESTAS____________________________________________________________________
            val scrollState = rememberScrollState()
            val headers by viewModel.headers.collectAsState();
            Row(modifier = border.height(40.dp)) {
                Box(
                    modifier = border.width(50.dp)
                        .fillMaxHeight()
                ) {
                    Checkbox(
                        checked = viewModel.checkAll,
                        onCheckedChange = {viewModel.updateCheckAll(it)},
                        Modifier
                            .fillMaxSize(),
                        colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.set_secundary)),
                    )
                    Text(text = listData.size.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp)
                            //.align(Alignment.Center) // Posiciona el texto al centro del contenedor
                            .zIndex(1f), // Asegura que el texto esté al frente
                        //color = Color.LightGray, // Opcional, para mejor visibilidad
                        //textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }

                Row(
                    Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState)
                ){
                    headers.forEach{header->
                        Text(
                            text = header.title,
                            modifier = border.width(header.width.dp)
                                .fillMaxHeight()
                                .wrapContentHeight(Alignment.CenterVertically)
                                .clickable {
                                    Log.i("propuesta:header",header.sort.toString())
                                    if(header.sort==null){
                                        viewModel.updateSortHeader(header.title,true,header.dataIndexStg)
//                                        header.sort=true;
                                    }else{
                                        viewModel.updateSortHeader(header.title,!header.sort!!,header.dataIndexStg)
//                                        header.sort = !header.sort!!
                                    }
                                },
                            textAlign = TextAlign.Center,
                            color = if(header.sort==true) Color.Blue else if(header.sort==false) Color.Red else Color.Black,


                        )

                    }
                }

                Text(
                    "Mas", modifier = border.width(50.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,

                )

            }


            if(viewModel.progressbarPropuestasState.isLoading){
                Column(modifier = border
                    .fillMaxHeight()
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    CircularProgressIndicator()
//                    Text("Cargando")
                    GProgressBar(viewModel.progressbarPropuestasState.message)
                }

            }else{
                LazyColumn(modifier = border
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    itemsIndexed(listData) { index,propuesta ->
                        Row(modifier = border.fillMaxWidth()) {
                            Box(
                                modifier = border
                                    .width(50.dp)
                                    .height(40.dp)
                            ){

                                Checkbox(
                                    checked = propuesta.checked,

                                    onCheckedChange = {viewModel.updateCheckState(propuesta.cve_control,it)},
                                    Modifier
                                        .fillMaxSize(),
                                    colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.set_secundary)),
                                )
                                Text(text = (index+1).toString(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 3.dp)
                                        //.align(Alignment.Center) // Posiciona el texto al centro del contenedor
                                        .zIndex(1f), // Asegura que el texto esté al frente
                                    //color = Color.LightGray, // Opcional, para mejor visibilidad
                                    //textAlign = TextAlign.Center,
                                    fontSize = 10.sp
                                )

                            }


                            Row(
                                Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .horizontalScroll(scrollState)
                            ) {
                                headers.forEach { header ->
                                    if(header.visible) {
                                        Box(
                                            modifier = border
                                                .width(header.width.dp)
                                                .fillMaxSize()
                                        ) {
                                            Text(
                                                header.dataIndex(propuesta),
                                                textAlign = TextAlign.Center,
                                                modifier =
                                                border
                                                    .fillMaxSize()
                                                    .wrapContentHeight(Alignment.CenterVertically)

                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = {
//                                val opcional = null;
                                viewModelDetallePropuesta.updateListDetalle(emptyList())
                                val webSocketRequest = WebSocketRequest(
                                    claveUsuario = viewModelLogin.user,
                                    claveControl = propuesta.cve_control,
                                    type = Constants.WS_TYPE_GET_DETALLE_BY_MOVIL,
                                    message = "Manda alerta al servidor de la api para traer el detalle de la propuesta"
                                )
                                webSocketClient2?.sendMessage(webSocketRequest)

                                navController.navigate(AppScreens.Detail.route+"/"+propuesta.cve_control)
//                            navController.navigate(AppScreens.Detail.route + "/${params}/?${opcional}")
                            }, modifier = border
                                .width(50.dp)
                                .height(40.dp)) {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24), // Cambia "tu_imagen" por el nombre de tu recurso
                                    contentDescription = "Botón de imagen",
                                    modifier = Modifier.size(24.dp), // Tamaño de la imagen
                                    colorFilter = ColorFilter.tint(Color.Gray) // Opcional: aplicar un tinte al ícono
                                )
                            }

                        }

                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }


            if (viewModel.showDialog) {
                DialogView(
                    "Cerrar",
                    onDismissRequest = {},
                    navController = navController,
                    viewoModelPropuestas = viewModel,
                    viewModelLogin = viewModelLogin
                ) {
                    viewModel.updateShowDialog(false)
                }
            }


//            BorderedTextFieldExample()
        }
    }


    //SE EJECUTA UNA VES CREADO EL COMPOSABLE Y CON UNIT SOLO SE CARGA UNA VES
    DisposableEffect(Unit) {
        onDispose {//CUANDO EL COMPOSE SE DESTRULLE SE MANDA LLAMAR ESTE EVENTO.
            //AQUI SE SUELEN DETENER EVENTOS DE CORRUTINAS ETC.
            //SOME ACTION
        }
    }

}
fun mainLogout(webSocket: WebSocketClient?,navController: NavController){
//    withContext(Dispatchers.IO) {
        webSocket?.closeConnection()
        navController.navigate(route = AppScreens.Login.route) {
            popUpTo(0);
        }
//    }
}
