import api from './api';

export interface BookSearchResponse {
    isbn: string;
    bookTitle: string;
    author: string;
    category: string;
    availableStock: number;
}

export interface BookAddRequest {
    isbn: string;
    bookTitle: string;
    author: string;
    description: string;
    category: string;
    totalStock: number;
    availableStock: number;
}

export const searchBooks = async (keyword?: string): Promise<BookSearchResponse[]> => {
    const response = await api.get('/books/search', {
        params: { keyword }
    });
    return response.data.data;
};

export const addBook = async (book: BookAddRequest): Promise<void> => {
    await api.post('/books/add', book);
};
