package com.company.ulpgcflix.ui.interfaces

import com.company.ulpgcflix.model.Category

interface CategoryRepository {
    fun getCategories(): List<Category>
}