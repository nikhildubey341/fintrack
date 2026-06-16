package com.fintrack.util;

public class AppConstants {

    private AppConstants() {}

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIR = "desc";

    public static final int MAX_PAGE_SIZE = 100;

    public static final String USER_NOT_FOUND = "User not found with id: ";
    public static final String CATEGORY_NOT_FOUND = "Category not found with id: ";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found with id: ";
    public static final String BUDGET_NOT_FOUND = "Budget not found with id: ";
    public static final String GOAL_NOT_FOUND = "Goal not found with id: ";

    public static final double BUDGET_WARNING_THRESHOLD = 80.0;
    public static final int TOP_EXPENSE_LIMIT = 5;
}
