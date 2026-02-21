import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Grid,
    Alert
} from '@mui/material';
import { addBook, BookAddRequest } from '../services/BookService';

interface AddBookDialogProps {
    open: boolean;
    onClose: () => void;
    onBookAdded: () => void;
}

const AddBookDialog: React.FC<AddBookDialogProps> = ({ open, onClose, onBookAdded }) => {
    const [book, setBook] = useState<BookAddRequest>({
        isbn: '',
        bookTitle: '',
        author: '',
        category: '',
        description: '',
        totalStock: 1,
        availableStock: 1
    });
    const [error, setError] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setBook(prev => ({
            ...prev,
            [name]: name === 'totalStock' || name === 'availableStock' ? parseInt(value) || 0 : value
        }));
    };

    const handleSubmit = async () => {
        try {
            setError(null);
            // 简单校验
            if (book.totalStock < book.availableStock) {
                setError('Available stock cannot exceed total stock.');
                return;
            }

            await addBook(book);
            onBookAdded();
            onClose();
            // Reset form
            setBook({
                isbn: '',
                bookTitle: '',
                author: '',
                category: '',
                description: '',
                totalStock: 1,
                availableStock: 1
            });
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to add book. Only admins can do this.');
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Add New Book</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
                <Grid container spacing={2} sx={{ mt: 1 }}>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="ISBN"
                            name="isbn"
                            value={book.isbn}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Title"
                            name="bookTitle"
                            value={book.bookTitle}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Author"
                            name="author"
                            value={book.author}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Category"
                            name="category"
                            value={book.category}
                            onChange={handleChange}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            fullWidth
                            type="number"
                            label="Total Stock"
                            name="totalStock"
                            value={book.totalStock}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            fullWidth
                            type="number"
                            label="Available Stock"
                            name="availableStock"
                            value={book.availableStock}
                            onChange={handleChange}
                            required
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            multiline
                            rows={3}
                            label="Description"
                            name="description"
                            value={book.description}
                            onChange={handleChange}
                        />
                    </Grid>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cancel</Button>
                <Button onClick={handleSubmit} variant="contained" color="primary">Add Book</Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddBookDialog;
