import { Text } from '@fluentui/react-components'
import { Navigate } from 'react-router-dom';
import './App.css'
import { useAuth } from './contexts/authContext'

function App() {

  const {token} = useAuth();

  if(token === null){
    return <Navigate to="/signin" />
  }

  return (
    <>
      <Text>Welcome!</Text>
    </>
  )
}

export default App
