package zlc.season.butterfly.entities

class BackStackEntry(val destinationData: DestinationData) {
    override fun toString(): String {
        return "{scheme=${destinationData.scheme}, class=${destinationData.className}}"
    }
}