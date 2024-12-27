import {useState} from 'react';
import {Card, Button, Input, Text} from '@fluentui/react-components';
import axios from 'axios';
import {AdminLoginPanel} from './LoginPanel.styled'

export const LoginPanel = () => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false);


    const submitLoggingIn = async() => {
        try {
            setError(false);
            const address = import.meta.env.VITE_API_URL+'auth/login';
            const result = await axios.post(address, {
                email,
                password
            }, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            console.log(result);
            localStorage.setItem('token', result.data.token);
        } catch {
            setError(true);
        }
    }

    return (
        <Card>
            <AdminLoginPanel>
                <Input 
                    type='text' 
                    placeholder='Email...' 
                    value={email}
                    onChange={(e) => setEmail(e.currentTarget.value)}
                />
                <Input 
                    type='password' 
                    placeholder='Password...'
                    value={password}
                    onChange={(e) => setPassword(e.currentTarget.value)}
                />
                {error && <Text>Login error</Text>}
                <Button onClick={() => submitLoggingIn()}>
                    Sign in
                </Button>
            </AdminLoginPanel>
        </Card>
    );
}