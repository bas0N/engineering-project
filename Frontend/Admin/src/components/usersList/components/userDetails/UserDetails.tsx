import { 
    Button,
    Carousel, 
    CarouselCard,
    CarouselNav, 
    CarouselNavButton, 
    CarouselNavContainer, 
    CarouselViewport,
    CarouselSlider,
    CarouselAnnouncerFunction
} from "@fluentui/react-components";
import { User } from "../../UsersList.helper";
import { UserDetailsWrapper } from './UserDetails.styled';
import { UserDetailsFrame } from "./userDetailsFrame/UserDetailsFrame";

interface UserDetails {
    users: User[];
    closeUserDetails: () => void;
}

export const UserDetails = ({
    users=[],
    closeUserDetails
}: UserDetails) => {
    const getAnnouncement: CarouselAnnouncerFunction = (
        index: number,
        totalSlides: number,
        ) => {
    return `Carousel slide ${index + 1} of ${totalSlides}`;
    };
    console.log(users);
    return (
        <UserDetailsWrapper>
            <Carousel 
                draggable
                groupSize={1}
                announcement={getAnnouncement}
            >
                <CarouselViewport>
                    <CarouselSlider>
                        {users.map((user) => (<CarouselCard>
                            <UserDetailsFrame user={user} />
                        </CarouselCard>))}
                    </CarouselSlider>
                </CarouselViewport>
                <CarouselNavContainer
                    layout="inline"
                    next={{ "aria-label": "go to next" }}
                    prev={{ "aria-label": "go to prev" }}
                >
                    <CarouselNav>
                        {(index) => (
                            <CarouselNavButton aria-label={`Carousel Nav Button ${index}`} />
                        )}
                    </CarouselNav>
                </CarouselNavContainer>
            </Carousel>
            <Button onClick={() => closeUserDetails()}>
                Close
            </Button>
        </UserDetailsWrapper>
    );
}