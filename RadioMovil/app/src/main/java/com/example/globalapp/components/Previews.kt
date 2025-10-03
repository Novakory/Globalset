package com.example.globalapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalapp.R
import com.example.globalapp.navegation.AppScreens
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 @Preview(showBackground = true)
fun scanfoldPreview(){
    Scaffold(
        topBar = {
            TopAppBar(
//                CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.set_secundary),
                    titleContentColor = Color.LightGray,
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
//                    Column{
                    Box(
                    ) {
                        Text("Principal")
                        Box(
//                            Modifier.align(Alignment.End)
//                            Modifier.padding(top = 20.dp),
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
                                        .size(18.dp, 18.dp)
                                        .align(Alignment.CenterVertically)
//                                    .align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    "ADMIN",
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }

//                    }
                },
                actions = {
                    Row (){
                        Box(Modifier.padding(end = 10.dp)){
                            BadgedBox (
                                badge = {
                                    Badge(){
                                        Text("2")
                                    }
                                }
                            ) {
                                IconButton(
                                    onClick = {
                                    },
                                    modifier = Modifier//.border(1.dp,Color.LightGray)
                                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))

                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_update_24),
                                        contentDescription = "Logout",
                                        tint = Color.LightGray,
                                    )
                                }
                            }
                        }

                        Column() {
                            IconButton(
                                onClick = {
                                },
                                modifier = Modifier//.border(1.dp,Color.LightGray)
                                    .border(
                                        1.dp,
                                        Color.LightGray,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .align(Alignment.CenterHorizontally)
                                    .width(105.dp)

                            ) {
                                Column {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_logout_24),
                                        contentDescription = "Logout",
                                        tint = Color.LightGray,
                                        modifier = Modifier//.border(1.dp,Color.Black)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Text(
                                        "Cerrar sesión",
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
//                        Text("Cerrar session")
                    }

                }
            )
        },
        floatingActionButton = {
            Column(modifier = Modifier
                    // .fillMaxWidth()
                //.border(BorderStroke(1.dp, Color.Red))
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


                            //.background(colorResource(R.color.set_primary3))
                            .background(colorResource(R.color.set_tertiary))
                            .padding(end = 15.dp)
                    ){
//                        Row(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .wrapContentHeight(Alignment.CenterVertically),
////                            horizontalArrangement = Arrangement.Center
//                            //horizontalArrangement = Arrangement.SpaceEvenly
//
//                        ){
//                            Text("(3)",
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier
//                                    //.border(BorderStroke(1.dp,Color.Gray))
//                                    .width(50.dp)
//                            )
//                            Text("Total:",
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier
//                                    .padding(end = 12.dp)
//                            )
//                            Text("$100,000.00",
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Right,
//                                modifier = Modifier
//                            )
//                        }


                    }

                    Box(
                        modifier = Modifier
                            //.border(BorderStroke(1.dp, Color.Blue))
                            //.padding(end = 8.dp)//.width()
                            .offset(x = -15.dp)
                    ){
                        FloatingActionButton(
                            onClick = {
                            },
                            //containerColor = Color.LightGray,
                            //containerColor = colorResource(R.color.set_secundary),
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
                                "Autorizar",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 33.dp)
                            )

                        }
                    }
                }

            }

        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
//                .fillMaxSize()
                .padding(8.dp)
                .border(BorderStroke(1.dp, Color.Gray))
                // .then(border),
//                .then(border),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@Composable
@Preview(showBackground = true)
fun topPrincipal(){
    val switchVar by remember { mutableStateOf(false) }
    Row(){
        Row(
            Modifier.weight(1f)
                .padding(start = 4.dp),
        ){

            Text("Autorizar",

                fontSize = 18.sp, modifier = Modifier
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically))
            Switch(checked = true,
                colors = SwitchDefaults.colors(checkedTrackColor = colorResource(R.color.set_secundary))
                , onCheckedChange = {

            })

        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp, end = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            val search by remember { mutableStateOf("") }
            SimpleTextField(search,"", enabled = true, modifier = Modifier.fillMaxWidth(1f)) {}
        }
    }

}

@Composable
fun SwipeToRefreshExample() {
    var refreshing by remember { mutableStateOf(false) }
    val list = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

//    SwipeToRefreshExample()
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            // Simula la recarga de datos, puedes reemplazarlo con tu lógica real
            refreshing = true
            refreshData(list) { newItems ->
                list.clear()
                list.addAll(newItems)
                refreshing = false
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(list) { item ->
                Text(item)
            }
        }
    }
}

fun refreshData(list: MutableList<String>, onComplete: (List<String>) -> Unit) {
    // Simula un retraso en la carga de los datos
//    LaunchedEffect(Unit) {
//        delay(2000) // Simula la carga de datos
//        onComplete(listOf("New Item 1", "New Item 2", "New Item 3"))
//    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSwipeToRefreshExample() {
    SwipeToRefreshExample()
}