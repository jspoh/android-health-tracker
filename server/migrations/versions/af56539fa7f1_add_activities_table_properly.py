"""add activities table (properly)

Revision ID: af56539fa7f1
Revises: 108eaa9d5d85
Create Date: 2026-03-24 20:42:24.784005

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'af56539fa7f1'
down_revision: Union[str, Sequence[str], None] = '108eaa9d5d85'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
