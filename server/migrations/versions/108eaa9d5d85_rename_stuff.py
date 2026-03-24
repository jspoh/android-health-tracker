"""rename stuff

Revision ID: 108eaa9d5d85
Revises: a60326511213
Create Date: 2026-03-24 20:40:41.863272

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '108eaa9d5d85'
down_revision: Union[str, Sequence[str], None] = 'a60326511213'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
