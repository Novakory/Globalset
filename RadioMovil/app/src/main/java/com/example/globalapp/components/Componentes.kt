package com.example.globalapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalapp.R
import com.example.globalapp.util.Constants

@Composable
fun MyFloatingActionButton(
    onClick:()->Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
    enabled:Boolean=true,
    content: @Composable () -> Unit,
){
    FloatingActionButton(
        onClick={
            if(enabled)  onClick();
        },
        modifier=modifier.then(Modifier.alpha(if (enabled) 1f else 0.5f) ),
        shape,
        containerColor,
        contentColor,
        elevation,
        interactionSource,
    ) {
        content()
    }
}

@Composable
fun TextAlert(message:String,typeIco:Int=0,modifier:Modifier = Modifier,textAlign: TextAlign = TextAlign.Start) {
    var colorText = Color.Black;
    if(typeIco!=0) {
        Image(
            painter = painterResource(
                id = if(typeIco===Constants.ICO_WARNING) R.drawable.baseline_warning_24
                else if(typeIco===Constants.ICO_WARNING) R.drawable.baseline_error_24
                else R.drawable.baseline_warning_24),
            contentDescription = "logo",
            modifier =
            Modifier
                .size(30.dp)
                .clip(CircleShape)//HACE QUE SEA CIRCULAR
            //.background(Color.Gray)
        )
        colorText = if(typeIco===Constants.ICO_WARNING) Color.Red
        else if(typeIco===Constants.ICO_WARNING) Color.Red
        else Color.Red;
    }

    Text(message, color = colorText, modifier = modifier,
        textAlign = textAlign)
}
@Composable
fun GProgressBar(message:String="",modifierText:Modifier = Modifier,modifierProgress:Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifierProgress)
    if(message.isNotBlank())  Text(message, modifier = modifierText)
}


@Composable
fun GSwitch(title:String,value:Boolean,modifier: Modifier = Modifier,onCheckedChange:(Boolean)->Unit){
    Row(
        Modifier.padding(start = 4.dp).then(modifier),
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 18.sp, modifier = Modifier
                .padding(end = 4.dp)

                .align(Alignment.CenterVertically)
        )
        Switch(checked = value, colors = SwitchDefaults.colors(checkedTrackColor = colorResource(R.color.set_secundary)),
            modifier = Modifier
            .scale(.7f)
            .height(24.dp)
            .width(40.dp)
            .align(Alignment.CenterVertically)
//                .border(BorderStroke(1.dp,Color.Red))
            , onCheckedChange = {
                onCheckedChange(it)
            })
    }
}
@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun GSwitchPreview(){
    Box() {
        Row(
            Modifier
//                .padding(start = 4.dp)
//                .height(40.dp)
                .border(BorderStroke(1.dp, Color.Red))
        ) {
            Text(
                "Autorizar",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp, modifier = Modifier
                    .padding(end = 6.dp)
                .align(Alignment.CenterVertically)
                    .border(BorderStroke(1.dp,Color.Red))
            )
            Switch(checked = false
                ,modifier = Modifier
                    .scale(.7f)
                    .height(1.dp)
                    .width(40.dp)
                    .align(Alignment.CenterVertically)
                    .border(BorderStroke(1.dp,Color.Red))
                , onCheckedChange = {
//            onCheckedChange(it)
            })
        }
    }
}
