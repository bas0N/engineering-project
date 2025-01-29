import { Image, Text } from '@fluentui/react-components';
import { User } from "../../../UsersList.helper";
import { 
    UserDetailsTableContainer, 
    UserDetailsTableRow,
    UserDetailsWrapper
} from "./UserDetailsFrame.styled";

interface UserDetailsFrameProps {
    user: User;
}

const userPropertiesToDisplay = ['id', 'email', 'firstName', 'lastName', 'phoneNumber', 'role'];

export const UserDetailsFrame = ({user}: UserDetailsFrameProps) => (
    <UserDetailsWrapper>
        <Text as='h2' size={700} align="center" weight="semibold">{user.email}</Text>
        <Image src={user.imageUrl ?? ''} alt={`No image present for user ${user.email}`} />
        <UserDetailsTableContainer>
            {
                userPropertiesToDisplay.map((key) => (
                    <UserDetailsTableRow>
                        <Text>
                            {key}
                        </Text>
                        <Text>
                            {
                                user[key as keyof User] === null 
                                    ? 'No data' 
                                    : user[key as keyof User]
                            }
                        </Text>
                    </UserDetailsTableRow>
                ))
            }
        </UserDetailsTableContainer>
    </UserDetailsWrapper>
);