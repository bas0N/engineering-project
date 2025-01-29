import styled from 'styled-components';

export const UserDetailsWrapper = styled.section`
    padding: 10px;
    width: calc(100vw - 20px);
    height: calc(100vh - 20px);
    position: fixed;
    top: 0;
    left: 0;
    background: rgba(0,0,0,.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    z-index: 2;
`;