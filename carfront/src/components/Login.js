import { Button, Snackbar, Stack, TextField } from '@mui/material';
import React, { useState } from 'react';
import { SERVER_URL } from '../constants';
import Carlist from './Carlist';

const Login = () => {
  const [user, setUser] = useState({
    username: '',
    password: '',
  });

  const [isAuthenticated, setAuth] = useState(false);

  const handleChange = (event) => {
    setUser({ ...user, [event.target.name]: event.target.value });
  };

  const [open, setOpen] = useState(false);

  const login = () => {
    // 로그인 post 요청 및 토큰 받기
    fetch(SERVER_URL + 'login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user),
    })
      .then((res) => {
        const jwtToken = res.headers.get('Authorization');
        if (jwtToken !== null) {
          sessionStorage.setItem('jwt', jwtToken); // 로그인을 브라우저 닫아도 유지시키려면 localStorage에 넣는 것이 좋다.
          setAuth(true);
        } else {
          setOpen(true);
        }
      })
      .catch((err) => console.log(err));
  };

  if (isAuthenticated) {
    return <Carlist />;
  } else {
    return (
      <div>
        <Stack spacing={2} alignItems="center" mt={2}>
          <TextField name="username" label="Username" onChange={handleChange} />
          <TextField
            type="password"
            name="password"
            label="Password"
            onChange={handleChange}
          />
          <Button varient="outlined" color="primary" onClick={login}>
            Login
          </Button>
        </Stack>
        <Snackbar
          open={open}
          autoHideDuration={3000}
          onClose={() => setOpen(false)}
          message="Login failed: Check your username and password"
        />
      </div>
    );
  }
};

export default Login;
