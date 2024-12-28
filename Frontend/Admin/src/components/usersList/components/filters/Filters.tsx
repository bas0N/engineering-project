import { Button } from "@fluentui/react-components";
import { ChangeEvent } from "react";
import { UsersFilterButtonsWrapper, UsersFilterInput, UsersListControlsWrapper } from "./Filters.styled";

export interface FiltersProps {
    filter: string;
    handleFilterChange: (newFilter: string) => void;
    deleteMarkedUsers: () => void;
    changeUsersRoles: () => void;
    triggerDetailsShowing: () => void;
    buttonsDisabled: boolean;
}

export const Filters = ({
    filter,
    handleFilterChange,
    deleteMarkedUsers,
    changeUsersRoles,
    triggerDetailsShowing,
    buttonsDisabled
} : FiltersProps) => (
    <UsersListControlsWrapper>
        <UsersFilterInput 
            placeholder="Enter filter value..."
            value={filter}
            onChange={(ev: ChangeEvent<HTMLInputElement>) => handleFilterChange(ev.currentTarget.value)}
        />
        <UsersFilterButtonsWrapper>
            <Button 
                onClick={() => deleteMarkedUsers()}
                disabled={buttonsDisabled}
            >
                Delete users
            </Button>
            <Button 
                onClick={() => triggerDetailsShowing()}
                disabled={buttonsDisabled}
            >
                Show details
            </Button>
            <Button 
                onClick={() => changeUsersRoles()}
                disabled={buttonsDisabled}
            >
                Change roles
            </Button>
        </UsersFilterButtonsWrapper>
    </UsersListControlsWrapper>
);