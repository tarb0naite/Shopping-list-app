package com.smallApps.Notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Face2
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Shower
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.smallApps.Notes.models.Category
import com.smallApps.Notes.models.ItemEntity
import com.smallApps.Notes.viewModel.ItemViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: ItemViewModel) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val categories = vm.categories
    val selectedCategory by vm.selectedCategory.collectAsState()
    val items by vm.items.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Kategorijos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                categories.forEach { cat ->
                    NavigationDrawerItem(
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(categoryIcon(cat), contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Text(categoryLabel(cat))
                            }
                        },
                        selected = cat == selectedCategory,
                        onClick = {
                            vm.selectedCategory(cat)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            categoryLabel(selectedCategory),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Outlined.Restaurant, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        TextButton(onClick = { vm.clearChecked() }) { Text("Clear ✓") }
                        TextButton(onClick = { vm.clearCategory() }) { Text("Clear all") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFEDEBFF),
                        titleContentColor = Color(0xFF2A2A2A)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                )

            },
            floatingActionButton = {
                FloatingActionButton(onClick = { vm.addItem("") }) {
                    Text("+")
                }
            }
        ) { padding ->
            ItemList(
                items = items,
                onToggle = { id -> vm.toggleItem(id) },
                onRename = { id, name -> vm.renameItem(id, name) },
                onDelete = { id -> vm.deleteItem(id) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun ItemList(
    items: List<ItemEntity>,
    onToggle: (Long) -> Unit,
    onRename: (Long, String) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("No items. Tap + to add.")
        }
        return
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemRow(
                item = item,
                onToggle = onToggle,
                onRename = onRename,
                onDelete = onDelete
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun ItemRow(
    item: ItemEntity,
    onToggle: (Long) -> Unit,
    onRename: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    var text by remember(item.id) { mutableStateOf(item.name) }
    val checked = item.checked

    LaunchedEffect(item.id, item.name) {
        if (item.name != text) text = item.name
    }
    LaunchedEffect(item.id) {
        snapshotFlow { text }
            .distinctUntilChanged()
            .debounce(250)
            .collect { latest -> onRename(item.id, latest) }
    }

    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onToggle(item.id) }
            )
            Spacer(Modifier.width(8.dp))
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("New item") },
                singleLine = true,
                textStyle = TextStyle(
                    textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "✕",
                modifier = Modifier
                    .clickable { onDelete(item.id) }
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@Composable
private fun categoryIcon(cat: Category) = when (cat) {
    Category.Daržovės          -> Icons.Outlined.Eco
    Category.Pieno_produktai   -> Icons.Outlined.LocalDrink
    Category.Mėsos_produktai   -> Icons.Outlined.Restaurant
    Category.Grudiniai_produktai -> Icons.Outlined.Face
    Category.Kuno_produktai    -> Icons.Outlined.Shower
    Category.Plaukų_produktai  -> Icons.Outlined.ContentCut
    Category.Veido_produktai   -> Icons.Outlined.Face2
    Category.Vitaminai  -> Icons.Outlined.Medication
    Category.Dideli_pirkiniai  -> Icons.Outlined.AttachMoney
}

private fun categoryLabel(cat: Category) = when (cat) {
    Category.Daržovės            -> "Daržovės"
    Category.Pieno_produktai     -> "Pieno produktai"
    Category.Mėsos_produktai     -> "Mėsos produktai"
    Category.Grudiniai_produktai -> "Grūdiniai produktai"
    Category.Kuno_produktai      -> "Kūno produktai"
    Category.Plaukų_produktai    -> "Plaukų produktai"
    Category.Veido_produktai     -> "Veido produktai"
    Category.Vitaminai     -> "Vitaminai"
    Category.Dideli_pirkiniai    -> "Dideli pirkiniai"
}
