import React, { useEffect, useState, useCallback } from 'react';
import { 
    Container, 
    Typography, 
    Button, 
    Box, 
    Paper, 
    TextField, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow,
    InputAdornment,
    Chip,
    Alert,
    Snackbar
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import BookIcon from '@mui/icons-material/MenuBook';
import { type BookSearchResponse, searchBooks, borrowBook } from '../services/BookService';
import AddBookDialog from '../components/AddBookDialog';

const HomePage: React.FC = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<{ username: string } | null>(null);
    const [books, setBooks] = useState<BookSearchResponse[]>([]);
    const [keyword, setKeyword] = useState('');
    const [openAddDialog, setOpenAddDialog] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMsg, setSuccessMsg] = useState<string | null>(null);

    const fetchBooks = useCallback(async () => {
        setError(null);
        try {
            const data = await searchBooks(keyword);
            setBooks(data);
        } catch (err: any) {
            console.error("Failed to fetch books", err);
            const message = err.response?.data?.message || err.message || "Failed to fetch books";
            setError(message);
        }
    }, [keyword]);

    useEffect(() => {
        const token = localStorage.getItem('access_token');
        if (!token) {
            navigate('/login');
        } else {
            // Mock user
            setUser({ username: 'Reader' });
        }
    }, [navigate]);

    const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
        setKeyword(e.target.value);
    };

    useEffect(() => {
        const timer = setTimeout(() => {
            fetchBooks();
        }, 300);
        return () => clearTimeout(timer);
    }, [fetchBooks]);

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        navigate('/login');
    };

    const handleBorrow = async (bookId: number) => {
        try {
            await borrowBook(bookId);
            setSuccessMsg("Borrow successful!");
            fetchBooks(); // Refresh to update stock
        } catch (err: any) {
            setError(err.message || "Borrow failed");
        }
    };

    return (
        <Container component="main" maxWidth="lg">
            <Box sx={{ mt: 4, mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="h4" component="h1">
                        Library Inventory
                    </Typography>
                    {user && <Typography variant="subtitle1" color="textSecondary">Welcome, {user.username}</Typography>}
                </Box>
                <Box>
                    <Button 
                        variant="contained" 
                        color="secondary" 
                        startIcon={<BookIcon />}
                        onClick={() => navigate('/my-books')}
                        sx={{ mr: 2 }}
                    >
                        My Books
                    </Button>
                    <Button variant="outlined" color="inherit" onClick={handleLogout} sx={{ mr: 2 }}>
                        Logout
                    </Button>
                    <Button 
                        variant="contained" 
                        color="primary" 
                        startIcon={<AddIcon />}
                        onClick={() => setOpenAddDialog(true)}
                    >
                        Add Book
                    </Button>
                </Box>
            </Box>

            {error && (
                <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}
            
            <Snackbar 
                open={!!successMsg} 
                autoHideDuration={4000} 
                onClose={() => setSuccessMsg(null)}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert severity="success" onClose={() => setSuccessMsg(null)}>
                    {successMsg}
                </Alert>
            </Snackbar>

            <Paper elevation={3} sx={{ p: 2, mb: 4 }}>
                <TextField
                    fullWidth
                    variant="outlined"
                    placeholder="Search by title or author..."
                    value={keyword}
                    onChange={handleSearch}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                    }}
                />
            </Paper>

            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>ISBN</TableCell>
                            <TableCell>Title</TableCell>
                            <TableCell>Author</TableCell>
                            <TableCell>Category</TableCell>
                            <TableCell align="right">Available</TableCell>
                            <TableCell align="center">Status</TableCell>
                            <TableCell align="center">Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {books.length > 0 ? (
                            books.map((book) => (
                                <TableRow
                                    key={book.id || book.isbn}
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                >
                                    <TableCell component="th" scope="row">
                                        {book.isbn}
                                    </TableCell>
                                    <TableCell>{book.bookTitle}</TableCell>
                                    <TableCell>{book.author}</TableCell>
                                    <TableCell>{book.category || '-'}</TableCell>
                                    <TableCell align="right">
                                        {book.availableStock}
                                    </TableCell>
                                    <TableCell align="center">
                                        <Chip 
                                            label={book.availableStock > 0 ? "Available" : "Out of Stock"} 
                                            color={book.availableStock > 0 ? "success" : "error"}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell align="center">
                                        <Button 
                                            size="small" 
                                            disabled={book.availableStock <= 0}
                                            variant="contained"
                                            color="primary"
                                            onClick={() => handleBorrow(book.id)}
                                        >
                                            Borrow
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={7} align="center">
                                    No books found.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            <AddBookDialog 
                open={openAddDialog} 
                onClose={() => setOpenAddDialog(false)} 
                onBookAdded={fetchBooks}
            />
        </Container>
    );
};

export default HomePage;
