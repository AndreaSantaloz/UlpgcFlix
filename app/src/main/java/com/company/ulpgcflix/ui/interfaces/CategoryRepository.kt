package com.company.ulpgcflix.ui.interfaces

import com.company.ulpgcflix.ui.model.CategoryUi

interface CategoryRepository {
    fun getCategories(): List<CategoryUi>
}