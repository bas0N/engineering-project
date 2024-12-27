import { Button } from "@fluentui/react-components";
import { ChangeEvent } from "react";
import { UsersFilterButtonsWrapper, UsersFilterInput, UsersListControlsWrapper } from "./Filters.styled";

export interface FiltersProps {
    filter: string;
    handleFilterChange: (newFilter: string) => void;
    deleteMarkedUsers: () => void;
    deletingDisabled: boolean;
}

export const Filters = ({
    filter,
    handleFilterChange,
    deleteMarkedUsers,
    deletingDisabled
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
                disabled={deletingDisabled}
            >
                Delete users
            </Button>
        </UsersFilterButtonsWrapper>
    </UsersListControlsWrapper>
);