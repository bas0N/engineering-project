import { Button, Input, Text } from "@fluentui/react-components";
import { styled } from "styled-components";

export const FeaturesWrapper = styled.section`
    width: calc(100% - 10px);
    padding: 10px 5px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const FeaturesInput = styled(Input).attrs({appearance: 'underline'})`
    width: calc(60% - 20px);
    padding: 10px;
    margin-bottom: 20px;
`;

export const FeaturesContainer = styled.div`
    width: calc(50% - 20px);
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
`;

export const FeatureWrapper = styled.div`
    width: calc(100% - 10px);
    padding: 5px;
    display: flex;
    justify-content: space-around;
    gap: 8px;
    align-items: center;
`;

export const FeatureText = styled(Text).attrs({size: 600})`
    flex: 1;
`;

export const FeatureOperations = styled.span`
    min-width: calc(10vw - 10px);
    width: fit-content;
    padding: 10px;
    display: flex;
    justify-content: center;
    gap: 8px;
`;

export const FeatureOperationButton = styled(Button).attrs({appearance: 'subtle'})`
    padding: 5px 0px;
`;