import axios from 'axios';
import { useState } from 'react';
import { Button, Divider, Text } from '@fluentui/react-components';
import { User } from "../../UsersList.helper";
import { 
    RolesWrapper, 
    RolesHeader, 
    RolesTableContainer,
    RoleWrapper,
    RoleButtonsWrapper,
    RoleEmail
} from "./Roles.styled";

interface RolesProps {
    users: User[];
    closeRolesPanel: () => void;
}

export const Roles = ({
    users,
    closeRolesPanel
} : RolesProps) => {

    const token = localStorage.getItem('token');

    const [error, setError] = useState(false);

    const changeRole = async (userId: number, newRole: string) => {
        try {
            setError(false);
            await axios.patch(`${import.meta.env.VITE_API_URL}auth/admin/change-roles/${userId}`, {
                userId,
                role: newRole
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
        } catch {
            setError(true);
        }
    }

    return (<RolesWrapper>
        <RolesHeader as='h2'>
            Change users roles
        </RolesHeader>
        <RolesTableContainer>
            {
                users.map((user) => (<>
                    <RoleWrapper>
                        <RoleEmail>
                            {user.email}
                        </RoleEmail>
                        <RoleEmail>
                            {user.role}
                        </RoleEmail>
                        <RoleButtonsWrapper>
                            <Button onClick={() => changeRole(user.id, 'ADMIN')}>
                                Make an admin
                            </Button>
                            <Button onClick={() => changeRole(user.id, 'USER')}>
                                Make a user
                            </Button>
                        </RoleButtonsWrapper>
                    </RoleWrapper>
                    <Divider />
                </>))
            }
        </RolesTableContainer>
        {
            error && <Text style={{color: 'red'}}>Failed to update the role</Text>
        }
        <Button onClick={() => closeRolesPanel()}>
            Close panel
        </Button>
    </RolesWrapper>);
}