import axios from "axios";
import { useEffect, useState } from "react";
import {
    createTableColumn,  
    DataGridHeader,
    DataGridRow,
    DataGridHeaderCell,
    DataGridBody,
    DataGridCell,
    Spinner, 
    TableCellLayout, 
    TableColumnDefinition, 
    Text, 
    TableRowId,
    DataGridProps,
    Tooltip,
} from '@fluentui/react-components';
import { 
  UsersDisplayInappropriateInfo,
  UsersListDataGrid,
} from "./UsersList.styled";

import { User, nullableStringsComparator, modifyTableText } from './UsersList.helper';
import { Filters } from "./components/filters/Filters";
import { Roles } from "./components/roles/Roles";

const columns:TableColumnDefinition<User>[] = [
    createTableColumn<User>({
        columnId: 'Id',
        compare: (a,b) => a.id > b.id ? 1 : a.id < b.id ? -1 : 0,
        renderHeaderCell: () => "User Id",
        renderCell: (user) => (<TableCellLayout>
          {user.id}
        </TableCellLayout>)
    }),
    createTableColumn<User>({
        columnId: 'firstName',
        compare: (a,b) => nullableStringsComparator(a, b, 'firstName'),
        renderHeaderCell: () => "First Name",
        renderCell: (user) => (<TableCellLayout>
          <Tooltip content={user.firstName ?? 'No first name precised'} relationship="label">
            <Text>
              {user.firstName === null ? '' : modifyTableText(user.firstName, 15)}
            </Text>
          </Tooltip>
        </TableCellLayout>)
    }),
    createTableColumn<User>({
        columnId: 'lastName',
        compare: (a,b) => nullableStringsComparator(a, b, 'lastName'),
        renderHeaderCell: () => "Last Name",
        renderCell: (user) => (<TableCellLayout>
          <Tooltip content={user.lastName ?? 'No first name precised'} relationship="label">
            <Text>
              {user.lastName === null ? '' : modifyTableText(user.lastName, 15)}
            </Text>
          </Tooltip>
        </TableCellLayout>)
    }),
    createTableColumn<User>({
        columnId: 'email',
        compare: (a,b) => nullableStringsComparator(a, b, 'email'),
        renderHeaderCell: () => "Email",
        renderCell: (user) => (<TableCellLayout>
          <Tooltip content={user.email} relationship="label">
            <Text>
              {modifyTableText(user.email, 15)}
            </Text>
          </Tooltip>
        </TableCellLayout>)
    }),
    createTableColumn<User>({
        columnId: 'role',
        compare: (a,b) => nullableStringsComparator(a, b, 'role'),
        renderHeaderCell: () => "Role",
        renderCell: (user) => (<TableCellLayout>
            {user.role}
        </TableCellLayout>)
    }),
]

export const UsersList = () => {
    
  const token = localStorage.getItem('token');
  const [error, setError] = useState(false);
  const [users, setUsers] = useState<User[] | null>(null);
  const [filter, setFilter] = useState('');
  const [rolesChangingPanelOpened, setRolesChangingPanelOpened] = useState(false);
  const [usersDetailsOpened, setUsersDetailsOpened] = useState(false);

  const [selectedRows, setSelectedRows] = useState(
    new Set<TableRowId>()
  );

  const onSelectionChange: DataGridProps["onSelectionChange"] = (_e, data) => {
    setSelectedRows(data.selectedItems);
  };

  useEffect(() => {
    if(token !== null) { 
      const getAllUsers = async() => {
        setError(false);
        try {
          const result = await axios.get(`${import.meta.env.VITE_API_URL}auth/admin/all-users`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          setUsers(result.data.content.map((user: Omit<User, 'displayScore'>) => ({
            ...user,
            displayScore: 1
          })));
        } catch {
          setError(true);
        }
      };

      getAllUsers();
    }
  }, [token]);

  const handleFilterChange = (newFilter: string) => {
    if(users !== null && newFilter.length > 0 ){
      // experimental filter with the use of Godel encoding
      const processedFilter = newFilter.trim().split(' ');
      const operand = users.map((user) => {
        let newScore = 1;
        processedFilter.forEach((filterPart) => {
          if(user.imageUrl?.toLowerCase().includes(filterPart.toLowerCase())) newScore*=2;
          if(user.firstName?.toLowerCase().includes(filterPart.toLowerCase())) newScore*=3;
          if(user.lastName?.toLowerCase().includes(filterPart.toLowerCase())) newScore*=5;
          if(user.email?.toLowerCase().includes(filterPart.toLowerCase())) newScore*=7;
          if(user.role?.toLowerCase().includes(filterPart.toLowerCase())) newScore*=11;
        });
  
        return ({
          ...user,
          displayScore: newScore === 1 ? 0 : newScore
        })
      });
      operand.sort((a, b) => a.displayScore > b.displayScore ? -1 : a.displayScore < b.displayScore ? 1 : 0);
      setUsers(operand);
    } else if (users !== null && newFilter.length === 0){
      const operand = users.map((user) => ({...user, displayScore: 1}));
      operand.sort((a, b) => a.id > b.id ? 1 : a.id < b.id ? -1 : 0);
      setUsers(operand);
    }
    setFilter(newFilter);
  }

  const deleteMarkedUsers = async() => {
    if (users !== null){
      selectedRows.forEach(async (row) => {
        try {
          await axios.delete(`${import.meta.env.VITE_API_URL}auth/admin/delete-user/${(users.find((user) => user.id === row) as User)?.uuid}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
        } catch (error) {
          console.log(error);
        }
      });
    }
  };

  const triggerUsersRoles = () => {
    setRolesChangingPanelOpened(false);
    setUsersDetailsOpened(true);
  };

  const changeUsersRoles = () => {
    setRolesChangingPanelOpened(true);
    setUsersDetailsOpened(false);
  };

  return (<>
    {
        error ? (<Text align='center' size={600}>Something went wrong. Try later</Text>) : 
        users === null ? (<Spinner label='Loading...' /> ) : (<>
            {
              rolesChangingPanelOpened && (<Roles 
                closeRolesPanel={() => setRolesChangingPanelOpened(false)} 
                users={users.filter((user) => selectedRows.has(user.id))}
              />)
            }
            <Filters 
              filter={filter}
              handleFilterChange={handleFilterChange}
              deleteMarkedUsers={deleteMarkedUsers}
              changeUsersRoles={changeUsersRoles}
              triggerDetailsShowing={triggerUsersRoles}
              buttonsDisabled={selectedRows.size === 0}
            />
            <UsersListDataGrid
              items={users.filter((user) => user.displayScore > 0)}
              columns={columns}
              sortable
              selectionMode="multiselect"
              getRowId={(item: User) => item.id}
              selectedItems={selectedRows}
              onSelectionChange={onSelectionChange}
              focusMode="composite"
            >
              <DataGridHeader>
                <DataGridRow
                  selectionCell={{
                    checkboxIndicator: { "aria-label": "Select all rows" },
                  }}
                >
                  {({ renderHeaderCell }) => (
                    <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
                  )}
                </DataGridRow>
              </DataGridHeader>
              <DataGridBody<User>>
                {({ item, rowId }) => (
                  <DataGridRow<User>
                    key={rowId}
                    selectionCell={{
                      checkboxIndicator: { "aria-label": "Select row" },
                    }}
                  >
                    {({ renderCell }) => (
                      <DataGridCell>{renderCell(item)}</DataGridCell>
                    )}
                  </DataGridRow>
                )}
              </DataGridBody>
            </UsersListDataGrid>
            <UsersDisplayInappropriateInfo>
              You need to have the device having the screen's with of at least 768px to be capable of handling the admin panel
            </UsersDisplayInappropriateInfo>
          </>
        )
    }
  </>);
}