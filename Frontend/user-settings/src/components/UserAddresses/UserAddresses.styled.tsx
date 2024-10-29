import styled from "styled-components";

export const UserAddressesAddingWrapper = styled.div`
    width: calc(80% - 20px);
    padding: 10px;
    text-align: center;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    align-items: center;
    justify-content: center;
`;

export const AddressesWrapper = styled.section`
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    align-self: center;
    gap: 8px;
    padding: 30px 20px 10px;

    @media screen and (min-width: 768px) {
        grid-template-columns: repeat(2, 1fr);
    }

    @media screen and (min-width: 1024px){
        grid-template-columns: repeat(3, 1fr);
    }
`;

export const AddressContainer = styled.div`
    padding: 20px;
    border-radius: 10px;
    background: #222222a0;
    display: flex;
    justify-content: center;
    flex-direction: row;
    flex: 1;
    align-items: center;
`;

export const AddressContent = styled.div`
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export const AddressOptions = styled.div`
    width: 60px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
`;