package com.avicodes.deardiary.presentation.components

import android.graphics.drawable.shapes.Shape
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avicodes.deardiary.model.Diary
import com.avicodes.deardiary.model.Mood
import com.avicodes.deardiary.ui.theme.Elevation
import com.avicodes.deardiary.utils.toInstant
import io.realm.kotlin.ext.realmListOf
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@Composable
fun DiaryHolder(
    diary: Diary,
    onClick: (String) -> Unit
) {
    var componentHeight by remember {
        mutableStateOf(0.dp)
    }

    var galleryOpened by remember {
        mutableStateOf(false)
    }
    val localDensity = LocalDensity.current

    Row(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember {
                MutableInteractionSource()
            }
        ) { onClick(diary._id.toString()) },
    ) {
        Spacer(modifier = Modifier.width(14.dp))

        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = Elevation.LEVEL1
        ) {

        }
        Spacer(modifier = Modifier.width(20.dp))

        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    componentHeight = with(localDensity) {
                        it.size.height.toDp()
                    }
                },
            tonalElevation = Elevation.LEVEL1
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {
                DiaryHeader(moodName = diary.mood, time = diary.date.toInstant())
                Text(
                    text = diary.description,
                    modifier = Modifier.padding(all = 14.dp),
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                if (diary.images.isNotEmpty()) {
                    ShowGalleryButton(galleryOpened = galleryOpened,
                        onClick = {
                            galleryOpened = !galleryOpened
                        })
                }

                AnimatedVisibility(visible = galleryOpened) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Gallery(images = diary.images)
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryHeader(
    moodName: String,
    time: Instant
) {

    val mood by remember {
        mutableStateOf(Mood.valueOf(moodName))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(mood.containerColor)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = mood.icon),
                contentDescription = "Mood Icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = mood.name,
                color = mood.contentColor,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )

        }

        Text(
            text = SimpleDateFormat("hh::mm a", Locale.US)
                .format(Date.from(time)),
            color = mood.contentColor,
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        )
    }

}

@Composable
fun ShowGalleryButton(
    galleryOpened: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = if (galleryOpened) "Hide Gallery" else "Show Gallery",
            style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize)
        )
    }
}

@Preview
@Composable
fun previewDiaryHolder() {
    DiaryHolder(diary = Diary().apply {
        title = "My diary"
        description =
            "asfdjasldfkajdfkljadflkk akfldjdfkls dlkf dskljdf kladjf dklsfjdfkl dsflk flkdjdf lkjdsf sakfljdflk lkf a"
        mood = Mood.Happy.name
        images = realmListOf("","")
    }, onClick = {})
}