package com.company.ulpgcflix.model

data class Group(
    private final val id: String,
    private final val name: String,
    private final val image:String,
    private final val idowner:String,
    private  val description:String,
    private val isPublic:Boolean=false,
){

    val getId: String
        get() = this.id

    val getDescription: String
        get() = this.description

    val getName: String
        get() = this.name

    val getIdOwner: String
        get() = this.idowner

    val getImage: String
        get() = this.image
}



