import styled from 'styled-components';

export const UserDetailsWrapper = styled.div`
    height: calc(80vh - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const UserDetailsTableContainer = styled.div`
    width: calc(80% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
`;

export const UserDetailsTableRow = styled.div`
    width: calc(100% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
`;