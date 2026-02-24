import api from './api';

export interface BookSearchResponse {
    id: number;
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

export interface BorrowRecordResponse {
    id: number;
    book: {
        id: number;
        bookTitle: string;
        author: string;
    };
    borrowDate: string;
    dueDate: string;
    returnDate: string | null;
    status: number;
    renewCount: number;
}

export const searchBooks = async (keyword?: string): Promise<BookSearchResponse[]> => {
    const response = await api.get('/books/search', {
        params: { keyword }
    });
    if (response.data.code && response.data.code !== 200) {
        throw new Error(response.data.message || 'Search failed');
    }
    return response.data.data || [];
};

export const addBook = async (book: BookAddRequest): Promise<void> => {
    await api.post('/books/add', book);
};

export const borrowBook = async (bookId: number): Promise<void> => {
    const response = await api.post(`/borrow/${bookId}`);
    if (response.data.code && response.data.code !== 200) {
        throw new Error(response.data.message || 'Borrow failed');
    }
};

export const returnBook = async (bookId: number): Promise<void> => {
    const response = await api.post(`/borrow/return/${bookId}`);
    if (response.data.code && response.data.code !== 200) {
        throw new Error(response.data.message || 'Return failed');
    }
};

export const renewBook = async (bookId: number): Promise<void> => {
    const response = await api.post(`/borrow/renew/${bookId}`);
    if (response.data.code && response.data.code !== 200) {
        throw new Error(response.data.message || 'Renew failed');
    }
};

export const getMyBorrowRecords = async (): Promise<BorrowRecordResponse[]> => {
    const response = await api.get('/borrow/my-borrowed-books');
    if (response.data.code && response.data.code !== 200) {
        throw new Error(response.data.message || 'Failed to fetch borrow records');
    }
    return response.data.data || [];
};
