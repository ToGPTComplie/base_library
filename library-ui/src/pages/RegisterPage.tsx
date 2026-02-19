import React, { useState } from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Box, Typography, Container, Alert, Link } from '@mui/material';
import api from '../services/api';
import { useNavigate, Link as RouterLink } from 'react-router-dom';

const RegisterPage: React.FC = () => {
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<boolean>(false);

    const formik = useFormik({
        initialValues: {
            username: '',
            password: '',
            confirmPassword: '',
            mobile: '',
            email: '',
            nickname: '',
        },
        validationSchema: Yup.object({
            username: Yup.string()
                .min(3, 'Username must be at least 3 characters')
                .max(64, 'Username must be at most 64 characters')
                .required('Username is required'),
            password: Yup.string()
                .min(6, 'Password must be at least 6 characters')
                .required('Password is required'),
            confirmPassword: Yup.string()
                .oneOf([Yup.ref('password')], 'Passwords must match')
                .required('Confirm Password is required'),
            mobile: Yup.string()
                .matches(/^[0-9]{11}$/, 'Mobile number must be 11 digits')
                .required('Mobile number is required'),
            email: Yup.string()
                .email('Invalid email address')
                .required('Email is required'),
            nickname: Yup.string()
                .required('Nickname is required'),
        }),
        onSubmit: async (values) => {
            setError(null);
            try {
                // Remove confirmPassword before sending to API
                const { confirmPassword, ...registerData } = values;
                await api.post('/auth/register', registerData);
                setSuccess(true);
                setTimeout(() => {
                    navigate('/login');
                }, 2000);
            } catch (err: any) {
                setError(err.response?.data?.message || 'Registration failed. Please try again.');
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
                    Sign up
                </Typography>
                
                {error && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ width: '100%', mt: 2 }}>Registration successful! Redirecting to login...</Alert>}

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
                        autoComplete="new-password"
                        value={formik.values.password}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.password && Boolean(formik.errors.password)}
                        helperText={formik.touched.password && formik.errors.password}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="confirmPassword"
                        label="Confirm Password"
                        type="password"
                        id="confirmPassword"
                        value={formik.values.confirmPassword}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.confirmPassword && Boolean(formik.errors.confirmPassword)}
                        helperText={formik.touched.confirmPassword && formik.errors.confirmPassword}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="mobile"
                        label="Mobile Number"
                        id="mobile"
                        value={formik.values.mobile}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.mobile && Boolean(formik.errors.mobile)}
                        helperText={formik.touched.mobile && formik.errors.mobile}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="email"
                        label="Email Address"
                        id="email"
                        value={formik.values.email}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.email && Boolean(formik.errors.email)}
                        helperText={formik.touched.email && formik.errors.email}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="nickname"
                        label="Nickname"
                        id="nickname"
                        value={formik.values.nickname}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.nickname && Boolean(formik.errors.nickname)}
                        helperText={formik.touched.nickname && formik.errors.nickname}
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={formik.isSubmitting}
                    >
                        Sign Up
                    </Button>
                    <Box sx={{ textAlign: 'center' }}>
                        <Link component={RouterLink} to="/login" variant="body2">
                            {"Already have an account? Sign in"}
                        </Link>
                    </Box>
                </Box>
            </Box>
        </Container>
    );
};

export default RegisterPage;
