package com.davgeoand.api.exception;

public class GroceryException {
    public static class MissingException extends NullPointerException {
        public MissingException(String string) {
            super(string);
        }
    }

    public static class MissingItemException extends MissingException {
        public MissingItemException(String itemId) {
            super("Item does not exist: " + itemId);
        }
    }

    public static class MissingCategoryException extends MissingException {
        public MissingCategoryException(String categoryId) {
            super("Category does not exist: " + categoryId);
        }
    }

    public static class MissingStoreException extends MissingException {
        public MissingStoreException(String storeId) {
            super("Store does not exist: " + storeId);
        }
    }
}
