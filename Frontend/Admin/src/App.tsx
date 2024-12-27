import { AppWrapper, AdminHeader } from './App.styled';
import { LoginPanel } from './components/loginPanel/LoginPanel';
import { UsersList } from './components/usersList/UsersList';

function App() {

  const token = localStorage.getItem('token')

  return (
    <AppWrapper>
      <AdminHeader as='h1'>Admin panel</AdminHeader>
      {
        token === null ?
          <LoginPanel />
        : (<UsersList />)
      }
    </AppWrapper>
  )
}

export default App
