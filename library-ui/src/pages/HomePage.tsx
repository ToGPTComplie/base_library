import React, { useEffect, useState } from 'react';
import { Container, Typography, Button, Box, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const HomePage: React.FC = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<any>(null);

    useEffect(() => {
        // Optional: Fetch user info if there's an endpoint for it
        // For now, just check if we have a token
        const token = localStorage.getItem('access_token');
        if (!token) {
            navigate('/login');
        } else {
            // Mock user data or decode token if needed
            // Here we could call an endpoint to get user profile
            setUser({ username: 'User' }); 
        }
    }, [navigate]);

    const handleLogout = () => {
        // Clear tokens
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        // Redirect to login
        navigate('/login');
    };

    return (
        <Container component="main" maxWidth="md">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Paper elevation={3} sx={{ p: 4, width: '100%', textAlign: 'center' }}>
                    <Typography component="h1" variant="h4" gutterBottom>
                        Welcome to the Library System
                    </Typography>
                    
                    {user && (
                        <Typography variant="h6" gutterBottom>
                            Hello, {user.username}!
                        </Typography>
                    )}

                    <Box sx={{ mt: 4 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleLogout}
                        >
                            Logout
                        </Button>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
};

export default HomePage;
