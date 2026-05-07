package com.example.mchavezmusicapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mchavezmusicapp.data.model.Album
import com.example.mchavezmusicapp.data.network.RetrofitClient
import com.example.mchavezmusicapp.navigation.Screen

val Purple = Color(0xFF7B2FBE)
val LightPurple = Color(0xFFB06FE8)

@Composable
fun HomeScreen(navController: NavController) {
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            albums = RetrofitClient.api.getAlbums()
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(Brush.verticalGradient(listOf(Purple, LightPurple)))
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            "Good Morning!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                        Text(
                            "Miguel Chavez",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Albums", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                    Text("See more", color = Purple, fontSize = 13.sp)
                }
            }

            item {
                when {
                    isLoading -> Box(
                        modifier = Modifier.fillMaxWidth().height(190.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Purple) }
                    error != null -> Box(
                        modifier = Modifier.fillMaxWidth().height(190.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Error al cargar", color = Color.Red) }
                    else -> LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(albums) { album ->
                            AlbumCard(album = album, onClick = {
                                navController.navigate(Screen.Detail.createRoute(album.id))
                            })
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recently Played", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                    Text("See more", color = Purple, fontSize = 13.sp)
                }
            }

            if (!isLoading && error == null) {
                items(albums) { album ->
                    RecentlyPlayedItem(album = album, onClick = {
                        navController.navigate(Screen.Detail.createRoute(album.id))
                    })
                }
            }
        }

        if (!isLoading && albums.isNotEmpty()) {
            MiniPlayer(
                album = albums.first(),
                isPlaying = isPlaying,
                onPlayPause = { isPlaying = !isPlaying },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun AlbumCard(album: Album, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(165.dp)
            .height(190.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = album.image,
            contentDescription = album.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                        startY = 80f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp, end = 40.dp)
        ) {
            Text(
                album.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                album.artist,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(34.dp)
                .clip(CircleShape)
                .background(Purple),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RecentlyPlayedItem(album: Album, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.image,
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    album.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${album.artist} • Popular Song",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun MiniPlayer(
    album: Album,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.image,
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    album.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    album.artist,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF2D1B4E),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}