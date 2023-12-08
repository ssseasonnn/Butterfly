package zlc.season.butterfly.entities

class GroupEntry(val destinationData: DestinationData) {
    override fun toString(): String {
        return "{scheme=${destinationData.scheme}, class=${destinationData.className}}"
    }
}