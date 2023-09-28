# Those steps simulate installing babarchka with dependencies

# Container
FROM archlinux

# Install packages
RUN pacman -Syu --noconfirm --needed base-devel
RUN pacman -S   --noconfirm --needed git

# Add new user
ARG user=testuser
RUN useradd --create-home $user \
  && echo "$user ALL=(ALL:ALL) NOPASSWD:ALL" > /etc/sudoers.d/$user

# Run as new user
USER $user
WORKDIR /home/$user

# Clone babarchka
RUN git clone https://github.com/caseneuve/babarchka.git
WORKDIR /home/$user/babarchka

# Install prerequisites
RUN ./pre/install-yay.sh
RUN ./pre/install-bb.sh

ENTRYPOINT ["./dockerfiles/entrypoint.sh"]
CMD ["bb test"]
