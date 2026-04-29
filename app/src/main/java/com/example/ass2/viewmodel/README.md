# viewmodel/
AndroidViewModel subclasses for each feature area.
Each ViewModel holds a Repository reference, exposes StateFlow/Flow to the UI layer,
and launches IO work via viewModelScope.launch(Dispatchers.IO).
