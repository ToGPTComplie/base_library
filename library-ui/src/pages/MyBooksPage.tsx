import React, { useEffect, useState } from 'react';
import { 
    Container, 
    Typography, 
    Box, 
    Paper, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow, 
    Button, 
    Chip, 
    Alert,
    CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { getMyBorrowRecords, returnBook, renewBook } from '../services/BookService';
import type { BorrowRecordResponse } from '../services/BookService';

const MyBooksPage: React.FC = () => {
    const navigate = useNavigate();
    const [records, setRecords] = useState<BorrowRecordResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const fetchRecords = async () => {
        setLoading(true);
        try {
            const data = await getMyBorrowRecords();
            setRecords(data);
            setError(null);
        } catch (err: any) {
            console.error(err);
            setError(err.message || "Failed to load borrow records");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRecords();
    }, []);

    const handleReturn = async (bookId: number) => {
        try {
            await returnBook(bookId);
            setSuccess("Book returned successfully");
            fetchRecords(); // Refresh list
        } catch (err: any) {
            setError(err.message || "Failed to return book");
        }
    };

    const handleRenew = async (bookId: number) => {
        try {
            await renewBook(bookId);
            setSuccess("Book renewed successfully");
            fetchRecords(); // Refresh list
        } catch (err: any) {
            setError(err.message || "Failed to renew book");
        }
    };

    // Format date string
    const formatDate = (dateStr: string) => {
        if (!dateStr) return '-';
        return new Date(dateStr).toLocaleDateString();
    };

    const getStatusChip = (status: number, dueDate: string) => {
        const isOverdue = new Date() > new Date(dueDate) && status === 0;
        
        if (status === 1) {
            return <Chip label="Returned" color="default" size="small" />;
        } else if (status === 2 || isOverdue) {
            return <Chip label="Overdue" color="error" size="small" />;
        } else {
            return <Chip label="Borrowed" color="primary" size="small" />;
        }
    };

    if (loading && records.length === 0) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg">
            <Box sx={{ mt: 4, mb: 4 }}>
                <Button 
                    startIcon={<ArrowBackIcon />} 
                    onClick={() => navigate('/')}
                    sx={{ mb: 2 }}
                >
                    Back to Library
                </Button>
                
                <Typography variant="h4" gutterBottom>
                    My Borrowed Books
                </Typography>
                
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                        {error}
                    </Alert>
                )}
                
                {success && (
                    <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
                        {success}
                    </Alert>
                )}

                <TableContainer component={Paper}>
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead>
                            <TableRow>
                                <TableCell>Book Title</TableCell>
                                <TableCell>Author</TableCell>
                                <TableCell>Borrow Date</TableCell>
                                <TableCell>Due Date</TableCell>
                                <TableCell>Return Date</TableCell>
                                <TableCell align="center">Status</TableCell>
                                <TableCell align="center">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {records.length > 0 ? (
                                records.map((record) => (
                                    <TableRow key={record.id}>
                                        <TableCell>{record.book.bookTitle}</TableCell>
                                        <TableCell>{record.book.author}</TableCell>
                                        <TableCell>{formatDate(record.borrowDate)}</TableCell>
                                        <TableCell>{formatDate(record.dueDate)}</TableCell>
                                        <TableCell>{record.returnDate ? formatDate(record.returnDate) : '-'}</TableCell>
                                        <TableCell align="center">
                                            {getStatusChip(record.status, record.dueDate)}
                                        </TableCell>
                                        <TableCell align="center">
                                            {record.status === 0 && ( // Only show actions if not returned
                                                <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center' }}>
                                                    <Button 
                                                        size="small" 
                                                        variant="contained" 
                                                        color="primary"
                                                        onClick={() => handleReturn(record.book.id)}
                                                    >
                                                        Return
                                                    </Button>
                                                    <Button 
                                                        size="small" 
                                                        variant="outlined" 
                                                        onClick={() => handleRenew(record.book.id)}
                                                        disabled={record.renewCount >= 1}
                                                    >
                                                        Renew {record.renewCount > 0 && `(${record.renewCount})`}
                                                    </Button>
                                                </Box>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={7} align="center">
                                        You haven't borrowed any books yet.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
        </Container>
    );
};

export default MyBooksPage;
