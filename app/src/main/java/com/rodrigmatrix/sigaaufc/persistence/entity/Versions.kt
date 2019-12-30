package com.rodrigmatrix.sigaaufc.persistence.entity

import com.google.gson.annotations.SerializedName

data class Versions(
    @SerializedName("versions")
    val versions: List<Version>
)