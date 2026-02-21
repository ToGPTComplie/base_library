import React, { useEffect, useState } from 'react';
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
    Chip
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import { BookSearchResponse, searchBooks } from '../services/BookService';
import AddBookDialog from '../components/AddBookDialog';

const HomePage: React.FC = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<any>(null);
    const [books, setBooks] = useState<BookSearchResponse[]>([]);
    const [keyword, setKeyword] = useState('');
    const [openAddDialog, setOpenAddDialog] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('access_token');
        if (!token) {
            navigate('/login');
        } else {
            // Mock user, or decode JWT
            setUser({ username: 'Reader' });
            fetchBooks();
        }
    }, [navigate]);

    const fetchBooks = async () => {
        try {
            const data = await searchBooks(keyword);
            setBooks(data);
        } catch (error) {
            console.error("Failed to fetch books", error);
        }
    };

    const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
        setKeyword(e.target.value);
        // In real app, debounce this
    };

    // Trigger search when keyword changes (simple implementation)
    useEffect(() => {
        const timer = setTimeout(() => {
            fetchBooks();
        }, 300); // 300ms debounce
        return () => clearTimeout(timer);
    }, [keyword]);

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        navigate('/login');
    };

    return (
        <Container component="main" maxWidth="lg">
            <Box sx={{ mt: 4, mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h4" component="h1">
                    Library Inventory
                </Typography>
                <Box>
                     <Button variant="outlined" color="secondary" onClick={handleLogout} sx={{ mr: 2 }}>
                        Logout
                    </Button>
                    {/* Assuming only admins can see this, but for now show to everyone, backend will block */}
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
                            <TableCell align="right">Available / Total</TableCell>
                            <TableCell align="center">Status</TableCell>
                            <TableCell align="center">Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {books.length > 0 ? (
                            books.map((book) => (
                                <TableRow
                                    key={book.isbn}
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                >
                                    <TableCell component="th" scope="row">
                                        {book.isbn}
                                    </TableCell>
                                    <TableCell>{book.bookTitle}</TableCell>
                                    <TableCell>{book.author}</TableCell>
                                    <TableCell>{book.category || '-'}</TableCell>
                                    <TableCell align="right">
                                        {book.availableStock} / {book.availableStock} {/* Note: Response might not have totalStock if strict DTO */}
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
                                            variant="outlined"
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
