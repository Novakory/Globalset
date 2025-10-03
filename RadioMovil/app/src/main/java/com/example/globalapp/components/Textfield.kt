package com.example.globalapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
//import androidx.compose.material.icons.Icons
//import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalapp.R

@Composable
fun OunTextField(value:String,label:String,enabled: Boolean = true, modifier: Modifier = Modifier,onValueChange: (String)->Unit){
    androidx.compose.material3.TextField(
        value = value,
        onValueChange = onValueChange, // Actualiza el estado cuando el texto cambia
        label = { Text(label) }, // Etiqueta para el campo
        singleLine = true,// Hace que el TextField sea de una sola línea
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Oculta la línea inferior cuando está enfocado
            unfocusedIndicatorColor = Color.Transparent // Oculta la línea inferior cuando no está enfocado
        )
    )
}
@Composable
fun OunTextFieldPassword(value:String,label:String,enabled: Boolean = true, modifier: Modifier = Modifier,onValueChange: (String)->Unit){
    var passwordVisible by remember { mutableStateOf(false) }
    androidx.compose.material3.TextField(
        value = value,
        onValueChange = onValueChange, // Actualiza el estado cuando el texto cambia
        label = { Text(label) }, // Etiqueta para el campo
        singleLine = true,// Hace que el TextField sea de una sola línea
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Oculta la línea inferior cuando está enfocado
            unfocusedIndicatorColor = Color.Transparent // Oculta la línea inferior cuando no está enfocado
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {

                val icon = if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                Icon(painter = painterResource(icon), contentDescription = if (passwordVisible) "Hide password" else "Show password")
//                val icon = if (passwordVisible) Icons.Default.Favorite else Icons.Default.FavoriteBorder
//                Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
            }
        }
    )
}

@Composable
fun SimpleTextField2(value:String,label:String, modifier: Modifier = Modifier,onValueChange: (String)->Unit) {
    TextField(
        value = value,
        onValueChange,
        modifier = Modifier.size(100.dp, 40.dp)
            .border(BorderStroke(1.dp,Color.Gray)).then(modifier), // Tamaño compacto
        singleLine = true, // Solo una línea de texto
        placeholder = { Text(label) }, // Placeholder simple
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // Oculta la línea inferior cuando está enfocado
            unfocusedIndicatorColor = Color.Transparent // Oculta la línea inferior cuando no está enfocado
        ),
        textStyle = TextStyle.Default.copy(fontSize = 14.sp), // Ajusta el tamaño de fuente
//        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun SimpleTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled:Boolean = true,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            //.size(200.dp, 40.dp)
            .height(35.dp)
            //.fillMaxWidth(0.5f)
//            .clip(CircleShape)//HACE QUE SEA CIRCULAR
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
            .background(if(enabled) Color.White else Color.LightGray)
            .padding(horizontal = 4.dp, vertical = 10.dp) // Ajusta los paddings manualmente
            .then(modifier)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
                //.border(BorderStroke(1.dp, Color.Gray)),
            textStyle = TextStyle(fontSize = 11.sp, textAlign = TextAlign.Start
            //    , lineHeight = 100.sp
            ),
            enabled = enabled,
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = label,
                        modifier = Modifier.wrapContentHeight(Alignment.CenterVertically),
                        style = TextStyle(fontSize = 11.sp, color = Color.Gray)
                    )
                }
                innerTextField()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleTextFieldPreview(){
    var value by remember{mutableStateOf("")}
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        SimpleTextField(value, "Importe", modifier = Modifier.fillMaxWidth(0.5f)) {
            value = it
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SimpleTextField2Preview(){
    var value by remember{mutableStateOf("")}
    SimpleTextField2(value,"") {
        value=it
    }
}
//                    focusedContainerColor = Color.LightGray, // Color de fondo cuando está enfocado
//                    unfocusedContainerColor = Color.Gray, // Color de fondo cuando no está enfocado