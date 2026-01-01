import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.menuannam.FlashCard

@Composable
fun FlashCardList(
    selectedItem: (FlashCard) -> Unit,
    flashCards: List<FlashCard>
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp)
                    .clickable(onClick = {
                        selectedItem(flashCard)
                    }
                    )
            ) {
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.englishCard.toString()) }
                Column(modifier = Modifier.padding(6.dp)) { Text(" = ") }
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.vietnameseCard.toString()) }
            }
        }
    }
}


@Composable
fun SearchCardsScreen(
    flashCards: List<FlashCard>,
    selectedItem: (FlashCard) -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        FlashCardList(
            flashCards = flashCards,
            selectedItem = selectedItem
        )
    }
}