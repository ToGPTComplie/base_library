import React, { useState } from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Box, Typography, Container, Alert, Link } from '@mui/material';
import api from '../services/api';
import { useNavigate, Link as RouterLink } from 'react-router-dom';

const LoginPage: React.FC = () => {
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);

    const formik = useFormik({
        initialValues: {
            username: '',
            password: '',
        },
        validationSchema: Yup.object({
            username: Yup.string().required('Username is required'),
            password: Yup.string().required('Password is required'),
        }),
        onSubmit: async (values) => {
            setError(null);
            try {
                const response = await api.post('/auth/login', values);
                const { accessToken, refreshToken } = response.data.data;

                // 存储 Token
                localStorage.setItem('access_token', accessToken);
                localStorage.setItem('refresh_token', refreshToken);

                // 跳转到首页（目前先跳到个人中心或 Dashboard，暂时用 / 代替）
                navigate('/');
            } catch (err: any) {
                setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
            }
        },
    });

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>

                {error && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{error}</Alert>}

                <Box component="form" onSubmit={formik.handleSubmit} noValidate sx={{ mt: 1 }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={formik.values.username}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.username && Boolean(formik.errors.username)}
                        helperText={formik.touched.username && formik.errors.username}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={formik.values.password}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.password && Boolean(formik.errors.password)}
                        helperText={formik.touched.password && formik.errors.password}
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={formik.isSubmitting}
                    >
                        Sign In
                    </Button>
                    <Box sx={{ textAlign: 'center' }}>
                        <Link component={RouterLink} to="/register" variant="body2">
                            {"Don't have an account? Sign Up"}
                        </Link>
                    </Box>
                </Box>
            </Box>
        </Container>
    );
};

export default LoginPage;
