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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mchavezmusicapp.data.model.Album
import com.example.mchavezmusicapp.data.network.RetrofitClient
import com.example.mchavezmusicapp.navigation.Screen

val Purple = Color(0xFF7B2FBE)
val LightPurple = Color(0xFFB06FE8)
val DarkBg = Color(0xFF1A1A2E)

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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(listOf(Purple, LightPurple))
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text("Good Morning!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("Miguel Chavez", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Albums", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("See more", color = Purple, fontSize = 13.sp)
                }
            }

            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Purple)
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = Color.Red)
                    }
                } else {
                    LazyRow(
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recently Played", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
            .width(160.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
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
                .background(Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                ))
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
        ) {
            Text(album.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(album.artist, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Purple),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun RecentlyPlayedItem(album: Album, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.image,
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(album.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
                Text("${album.artist} • Popular Song", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun MiniPlayer(album: Album, isPlaying: Boolean, onPlayPause: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B4E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.image,
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(album.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                Text(album.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF2D1B4E),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}